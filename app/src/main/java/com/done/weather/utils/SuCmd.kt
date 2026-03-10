package com.done.weather.utils

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

/**
 * Мини-исполнитель команд через "su" без библиотек.
 * Работает только на рутованных устройствах (Magisk/SU).
 */
object SuCmd {

    data class Result(
        val code: Int,
        val out: String,
        val err: String
    ) {
        val isSuccess: Boolean get() = code == 0
    }

    /** Быстрая проверка рута */
    fun available(timeoutMs: Long = 1200): Boolean {
        return run("id", timeoutMs).isSuccess
    }

    /** Запуск одной команды */
    fun run(command: String, timeoutMs: Long = 30_000): Result {
        // Важно: запускаем "su", потом пишем команды в stdin и делаем exit
        val proc = try {
            Runtime.getRuntime().exec("su")
        } catch (t: Throwable) {
            return Result(-1, "", t.message ?: "su exec failed")
        }

        return try {
            DataOutputStream(proc.outputStream).use { os ->
                os.writeBytes(command)
                os.writeBytes("\n")
                os.writeBytes("exit\n")
                os.flush()
            }

            val finished = proc.waitFor(timeoutMs, TimeUnit.MILLISECONDS)
            if (!finished) {
                proc.destroy()
                return Result(-2, "", "timeout")
            }

            val out = proc.inputStream.bufferedReaderSafe()
            val err = proc.errorStream.bufferedReaderSafe()
            Result(proc.exitValue(), out, err)
        } catch (t: Throwable) {
            try { proc.destroy() } catch (_: Throwable) {}
            Result(-3, "", t.message ?: "unknown error")
        }
    }

    /** Запуск нескольких команд одним su-сеансом (быстрее и стабильнее) */
    fun runAll(commands: List<String>, timeoutMs: Long = 60_000): Result {
        val proc = try {
            Runtime.getRuntime().exec("su")
        } catch (t: Throwable) {
            return Result(-1, "", t.message ?: "su exec failed")
        }

        return try {
            DataOutputStream(proc.outputStream).use { os ->
                for (cmd in commands) {
                    os.writeBytes(cmd)
                    os.writeBytes("\n")
                }
                os.writeBytes("exit\n")
                os.flush()
            }

            val finished = proc.waitFor(timeoutMs, TimeUnit.MILLISECONDS)
            if (!finished) {
                proc.destroy()
                return Result(-2, "", "timeout")
            }

            val out = proc.inputStream.bufferedReaderSafe()
            val err = proc.errorStream.bufferedReaderSafe()
            Result(proc.exitValue(), out, err)
        } catch (t: Throwable) {
            try { proc.destroy() } catch (_: Throwable) {}
            Result(-3, "", t.message ?: "unknown error")
        }
    }

    private fun java.io.InputStream.bufferedReaderSafe(): String {
        return try {
            BufferedReader(InputStreamReader(this)).use { it.readText() }.trim()
        } catch (_: Throwable) {
            ""
        }
    }
}
