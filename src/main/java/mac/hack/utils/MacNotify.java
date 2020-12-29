package mac.hack.utils;

import mac.hack.MacHack;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class MacNotify {

    public static void Notifications(String subtitle, String message) throws IOException {
        String os = System.getProperty("os.name");
        String title = MacHack.NAME;
        Image image = ImageIO.read(MacNotify.class.getResource("/assets/machack/MacHack.png"));
        if (os.contains("Linux")) {
            ProcessBuilder builder = new ProcessBuilder(
                    "zenity",
                    "--notification",
                    "--title=" + title,
                    "--text=" + message);
            builder.inheritIO().start();
        } else if (os.contains("Mac")) {
            ProcessBuilder builder = new ProcessBuilder(
                    "osascript", "-e",
                    "display notification \"" + message + "\""
                            + " subtitle \"" + subtitle + "\""
                            + " with title \"" + title + "\"");
            builder.inheritIO().start();
        } else if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            TrayIcon icon = new TrayIcon(image, title);
            icon.setImageAutoSize(true);
            icon.setToolTip(title);
            try {
                tray.add(icon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
            icon.displayMessage(title, message, TrayIcon.MessageType.INFO);
        }
    }
}