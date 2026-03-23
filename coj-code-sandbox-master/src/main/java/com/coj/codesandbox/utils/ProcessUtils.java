package com.coj.codesandbox.utils;

import cn.hutool.core.util.StrUtil;
import com.coj.codesandbox.model.ExecuteMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 进程工具类
 */
public class ProcessUtils {

    /**
     * 执行进程并获取信息
     *
     * @param runProcess
     * @param opName
     * @return
     */
    public static ExecuteMessage runProcessAndGetMessage(Process runProcess, String opName) {
        ExecuteMessage executeMessage = new ExecuteMessage();

        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            // 等待程序执行，获取错误码
            int exitValue = runProcess.waitFor();
            executeMessage.setExitValue(exitValue);
            // 正常退出
            if (exitValue == 0) {
                System.out.println(opName + "成功");
                // 分批获取进程的正常输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream(), StandardCharsets.UTF_8));
                List<String> outputStrList = new ArrayList<>();
                // 逐行读取
                String compileOutputLine;
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    outputStrList.add(compileOutputLine);
                }
                executeMessage.setMessage(StringUtils.join(outputStrList, "\n"));
            } else {
                // 异常退出
                System.out.println(opName + "失败，错误码： " + exitValue);
                // 分批获取进程的正常输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream(), StandardCharsets.UTF_8));
                List<String> outputStrList = new ArrayList<>();
                // 逐行读取
                String compileOutputLine;
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    outputStrList.add(compileOutputLine);
                }
                executeMessage.setMessage(StringUtils.join(outputStrList, "\n"));

                // 分批获取进程的错误输出
                BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream(), StandardCharsets.UTF_8));
                // 逐行读取
                List<String> errorOutputStrList = new ArrayList<>();
                // 逐行读取
                String errorCompileOutputLine;
                while ((errorCompileOutputLine = errorBufferedReader.readLine()) != null) {
                    errorOutputStrList.add(errorCompileOutputLine);
                }
                executeMessage.setErrorMessage(StringUtils.join(errorOutputStrList, "\n"));
            }
            stopWatch.stop();
            executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return executeMessage;
    }

    /**
     * 执行交互式进程并获取信息
     *
     * @param runProcess
     * @param args
     * @return
     */
    public static ExecuteMessage runInteractProcessAndGetMessage(Process runProcess, String args) {
        ExecuteMessage executeMessage = new ExecuteMessage();

        try {
            // 向控制台输入程序
            OutputStream outputStream = runProcess.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            String[] s = args.split(" ");
            String join = StrUtil.join("\n", s) + "\n";
            outputStreamWriter.write(join);
            // 相当于按了回车，执行输入的发送
            outputStreamWriter.flush();

            // 分批获取进程的正常输出
            InputStream inputStream = runProcess.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder compileOutputStringBuilder = new StringBuilder();
            // 逐行读取
            String compileOutputLine;
            while ((compileOutputLine = bufferedReader.readLine()) != null) {
                compileOutputStringBuilder.append(compileOutputLine);
            }
            executeMessage.setMessage(compileOutputStringBuilder.toString());
            // 记得资源的释放，否则会卡死
            outputStreamWriter.close();
            outputStream.close();
            inputStream.close();
            runProcess.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return executeMessage;
    }

    /**
     * 执行交互式进程并获取信息（将 input 原样写入 stdin，支持多行输入）
     *
     * @param runProcess
     * @param opName
     * @param input 测试用例输入字符串，原样写入 stdin
     * @return
     */
    public static ExecuteMessage runInteractProcessAndGetMessage(Process runProcess, String opName, String input) {
        ExecuteMessage executeMessage = new ExecuteMessage();
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            OutputStream outputStream = runProcess.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            if (!input.endsWith("\n")) {
                input = input + "\n";
            }
            outputStreamWriter.write(input);
            outputStreamWriter.flush();
            outputStreamWriter.close();
            outputStream.close();

            int exitValue = runProcess.waitFor();
            stopWatch.stop();
            executeMessage.setExitValue(exitValue);
            executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
            if (exitValue == 0) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream(), StandardCharsets.UTF_8));
                List<String> outputLines = new ArrayList<>();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    outputLines.add(line);
                }
                executeMessage.setMessage(StringUtils.join(outputLines, "\n"));
            } else {
                BufferedReader errReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream(), StandardCharsets.UTF_8));
                List<String> errLines = new ArrayList<>();
                String line;
                while ((line = errReader.readLine()) != null) {
                    errLines.add(line);
                }
                executeMessage.setErrorMessage(StringUtils.join(errLines, "\n"));
            }
            runProcess.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return executeMessage;
    }
}
