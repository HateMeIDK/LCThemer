package ml.xelleri;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Main {
    static String hostname = "http://lcthemer.7m.pl";
    static String[] getThemes(){
        String[] themes = new String[]{};
        try {
            StringBuilder sb = new StringBuilder();
            URL url = new URL(hostname + "/themes.list");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            br.close();
            String data = sb.toString();
            if(data.length() == 0){
                themes = new String[]{"Default", "Custom(Specify path above)"};
            }else{
                themes = data.split(Pattern.quote("|"));
                for(int i = 0; i < themes.length; i++) {
                    themes[i] = themes[i].split(Pattern.quote(","))[0].substring(1);
                }
                themes = Stream.concat(Arrays.stream(new String[]{"Default", "Custom(Specify path above)"}), Arrays.stream(themes)).toArray(String[]::new);
            }
        }catch(Exception e) {
            e.printStackTrace();
            themes = new String[]{"Default", "Custom(Specify path above)"};
        }
        return themes;
    }
    static void initConfigFolder(){
        try {
            String basePath = "/home/"+System.getenv("USER")+"/.config/LCThemer";
            if(!(new File(basePath)).exists()) {
                System.out.println("LCThemer config folder not exists. Creating...");
                (new File(basePath)).mkdir();
            }
            if(!(new File(basePath + "/latestCustomTheme")).exists()){
                (new File(basePath + "/latestCustomTheme")).createNewFile();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    static String getCustomPath(){
        try {
            BufferedReader br = new BufferedReader(new FileReader("/home/"+System.getenv("USER")+"/.config/LCThemer/latestCustomTheme"));
            String data = br.readLine();
            br.close();
            return data;
        }catch (Exception e){
            return "";
        }
    }
    static void log(String text, JTextArea logger){
        logger.setText(logger.getText() + text + "\n");
    }
    static String getThemeLink(int idx){
        String _url = "";
        try {
            String[] themes = new String[]{};
            StringBuilder sb = new StringBuilder();
            URL url = new URL(hostname + "/themes.list");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            br.close();
            String data = sb.toString();
            if(data.length() == 0){
                themes = new String[]{};
            }else{
                themes = data.split(Pattern.quote("|"));
                for(int i = 0; i < themes.length; i++) {
                    themes[i] = themes[i].split(Pattern.quote(","))[1].substring(0, themes[i].split(Pattern.quote(","))[1].length()-1);
                }
                _url = themes[idx];
            }
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }
        return _url;
    }
    public static void main(String[] args) {
        initConfigFolder();
	    JFrame frame = new JFrame("LCThemer");
	    JLabel logLabel = new JLabel("Logs:");
	    logLabel.setBounds(0, 0, 200, 20);
	    logLabel.setForeground(Color.WHITE);
	    frame.add(logLabel);
	    JTextArea log = new JTextArea();
	    log.setBounds(0, 20, 200, 520);
        log.setEditable(false);
	    frame.add(log);
	    JList<String> themes = new JList<>();
	    themes.setBounds(210, 200, 400, 340);
	    String[] _themes = getThemes();
	    themes.setListData(_themes);
        frame.add(themes);
	    JLabel logo = new JLabel("LCThemer.");
	    logo.setBounds(210, 40, 400, 120);
	    logo.setForeground(Color.WHITE);
	    logo.setFont(new Font(logo.getFont().getName(), Font.PLAIN, 70));
	    frame.add(logo);
	    JButton startButton = new JButton();
	    startButton.setText("Start.");
	    startButton.setForeground(Color.WHITE);
	    startButton.setBackground(Color.GRAY);
	    startButton.setBounds(630, 500, 140, 40);
        JTextField customPath = new JTextField();
        customPath.setBounds(210, 140, 400, 40);
        customPath.setText(getCustomPath());
	    startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                log.setText("");
                try {
                    int idx = themes.getSelectedIndex();
                    if(idx == -1)return;
                    String basePath = "/home/"+System.getenv("USER")+"/.config/LCThemer";
                    if (idx == 1) {
                        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(basePath+"/latestCustomTheme")));
                        bw.write(customPath.getText());
                        bw.close();
                    }
                    if(idx == 1 && (!new File(customPath.getText()).exists() || (new File(customPath.getText()).exists() && new File(customPath.getText()).isDirectory()))){
                        log("[Custom] Invalid file.", log);
                        return;
                    }
                    if(!(new File(basePath + "/lunar.appimage")).exists()){
                        log("Downloading lunar.appimage...", log);
                        URL dl_url = new URL(hostname+"/lunar.appimage");
                        try {
                            BufferedInputStream bis = new BufferedInputStream(dl_url.openStream());
                            FileOutputStream fos = new FileOutputStream(basePath + "/lunar.appimage");
                            byte chunk[] = new byte[1024];
                            int br;
                            while((br = bis.read(chunk, 0, 1024)) != -1){
                                fos.write(chunk, 0, br);
                            }
                            log("Downloaded successfully.", log);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    if(!(new File(basePath + "/index.html")).exists()){
                        log("Downloading index.html...", log);
                        URL dl_url = new URL(hostname+"/index_patched.html");
                        try {
                            BufferedInputStream bis = new BufferedInputStream(dl_url.openStream());
                            FileOutputStream fos = new FileOutputStream(basePath + "/index.html");
                            byte chunk[] = new byte[1024];
                            int br;
                            while((br = bis.read(chunk, 0, 1024)) != -1){
                                fos.write(chunk, 0, br);
                            }
                            log("Downloaded successfully.", log);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    if(!(new File(basePath + "/package.json")).exists()){
                        log("Downloading package.json...", log);
                        URL dl_url = new URL(hostname+"/package_patched.json");
                        try {
                            BufferedInputStream bis = new BufferedInputStream(dl_url.openStream());
                            FileOutputStream fos = new FileOutputStream(basePath + "/package.json");
                            byte chunk[] = new byte[1024];
                            int br;
                            while((br = bis.read(chunk, 0, 1024)) != -1){
                                fos.write(chunk, 0, br);
                            }
                            log("Downloaded successfully.", log);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    Runtime.getRuntime().exec("chmod u+x "+basePath + "/lunar.appimage").waitFor();
                    log("Removing some files...", log);
                    Runtime.getRuntime().exec("rm -rf "+ basePath + "/squashfs-root").waitFor();
                    Runtime.getRuntime().exec("rm -rf " + System.getenv("PWD") + "/sqashfs-root").waitFor();
                    log("Success.", log);
                    log("Extracting AppImage...", log);
                    Runtime.getRuntime().exec(basePath + "/lunar.appimage --appimage-extract").waitFor();
                    log("Success.", log);
                    Runtime.getRuntime().exec("mv -f " + System.getenv("PWD") + "/squashfs-root "+basePath).waitFor();
                    log("Extracting resources...", log);
                    Runtime.getRuntime().exec("asar e "+ basePath + "/squashfs-root/resources/app.asar "+ basePath + "/squashfs-root/resources/app").waitFor();
                    log("Success.", log);
                    log("Patching index.html...", log);
                    Runtime.getRuntime().exec("rm -rf "+ basePath + "/squashfs-root/resources/app/index.html").waitFor();
                    Runtime.getRuntime().exec("cp -rf " + basePath + "/index.html " + basePath + "/squashfs-root/resources/app");
                    log("Success.", log);
                    log("Patching package.json...", log);
                    Runtime.getRuntime().exec("rm -rf "+ basePath + "/squashfs-root/resources/app/package.json").waitFor();
                    Runtime.getRuntime().exec("cp -rf " + basePath + "/package.json " + basePath + "/squashfs-root/resources/app");
                    log("Success.", log);
                    String _theme;
                    if(idx == 0){
                        _theme = "";
                    }else if(idx == 1){
                        _theme = "";
                        BufferedReader br = new BufferedReader(new FileReader(customPath.getText()));
                        StringBuilder sb = new StringBuilder();
                        String chunk;
                        while((chunk = br.readLine()) != null){
                            sb.append(chunk + "\n");
                        }
                        br.close();
                        _theme = sb.toString();
                        BufferedReader br1 = new BufferedReader(new FileReader(basePath + "/squashfs-root/resources/app/index.html"));
                        StringBuilder sb1 = new StringBuilder();
                        while((chunk = br1.readLine()) != null){
                            sb1.append(chunk.replace("{{CUSTOM_STYLES}}", _theme) + "\n");
                        }
                        br1.close();
                        String new_index = sb1.toString();
                        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(basePath + "/squashfs-root/resources/app/index.html")));
                        bw.write(new_index);
                        bw.close();
                    }else{
                        String themeLink = getThemeLink(idx - 2);
                        URL url = new URL(themeLink);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line; StringBuilder sb = new StringBuilder();
                        while((line = br.readLine()) != null){
                            sb.append(line + "\n");
                        }
                        br.close();
                        _theme = sb.toString();
                        BufferedReader br1 = new BufferedReader(new FileReader(basePath + "/squashfs-root/resources/app/index.html"));
                        StringBuilder sb1 = new StringBuilder(); String chunk;
                        while((chunk = br1.readLine()) != null){
                            sb1.append(chunk.replace("{{CUSTOM_STYLES}}", _theme) + "\n");
                        }
                        br1.close();
                        String new_index = sb1.toString();
                        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(basePath + "/squashfs-root/resources/app/index.html")));
                        bw.write(new_index);
                        bw.close();
                        System.out.println(new_index);
                    }
                    log("Starting...", log);
                    Runtime.getRuntime().exec(basePath + "/squashfs-root/lunarclient --no-sandbox");
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        frame.add(startButton);
	    frame.add(customPath);
	    frame.setSize(800, 600);
	    frame.setLayout(null);
	    frame.getContentPane().setBackground(Color.BLACK);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setResizable(false);
	    frame.setVisible(true);
    }
}
