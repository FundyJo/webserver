package jo.fundy.handlers;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jo.fundy.WebServer;
import jo.fundy.lib.ErrorPages;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class HttpServerHandler {

    public static JavaPlugin plugin = WebServer.getPlugin(WebServer.class);

    public static class ServerHandler implements HttpHandler {


        public void handle(HttpExchange t) throws IOException {
            HttpServerHandler.connections = HttpServerHandler.connections + 1;

            String serverHeader = "version 0.0.5 (beta)";
            String serverTime = HttpServerHandler.getServerTime();

            InputStream is = t.getRequestBody();
            String requestType = t.getRequestMethod();

            while (is.read() != -1) {
                is.skip(65536L);
            }
            is.close();

            if (HttpServerHandler.connections >= HttpServerHandler.plugin.getConfig().getInt("max-connections")) {


                if (HttpServerHandler.plugin.getConfig().getBoolean("kill-switch-enabled")) {

                    HttpServerHandler.plugin.getLogger().log(Level.SEVERE, "Connections: " + HttpServerHandler
                            .connections + "/" + HttpServerHandler.plugin.getConfig().getInt("max-connections") +
                            " | Initiating Kill-Switch. Server will return online in: " + HttpServerHandler
                            .plugin.getConfig().getInt("kill-switch-restart") + " ticks");
                    try {
                        HttpServerHandler.stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!HttpServerHandler.isRunning()) {
                        (new BukkitRunnable() {
                            public void run() {
                                try {
                                    HttpServerHandler.start();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).runTaskLater(HttpServerHandler.plugin, HttpServerHandler.plugin.getConfig().getLong("kill-switch-restart"));
                    }

                    return;
                }

                Headers headers = t.getResponseHeaders();
                headers.add("Server", serverHeader);
                headers.add("Date", serverTime);
                headers.add("Expires", HttpServerHandler.getExpiresTime(true));
                headers.add("Cache-Control", "Cache-Control: max-age=0, s-maxage=7200, must-revalidate");
                headers.add("Connection", "close");

                String response = "<!DOCTYPE html>\r\n<html>\r\n  <head>\r\n    <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js\"></script>\r\n    <script src=\"https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js\"></script>\r\n    <link href=\"https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,600\" media=\"screen\" rel=\"stylesheet\" />\r\n    <link href=\"https://maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css\" media=\"screen\" rel=\"stylesheet\" />\r\n\r\n\r\n    <style>\r\n      *{-moz-box-sizing:border-box;-webkit-box-sizing:border-box;box-sizing:border-box}html,body,div,span,object,iframe,h1,h2,h3,h4,h5,h6,p,blockquote,pre,abbr,address,cite,code,del,dfn,em,img,ins,kbd,q,samp,small,strong,sub,sup,var,b,i,dl,dt,dd,ol,ul,li,fieldset,form,label,legend,caption,article,aside,canvas,details,figcaption,figure,footer,header,hgroup,menu,nav,section,summary,time,mark,audio,video{margin:0;padding:0;border:0;outline:0;vertical-align:baseline;background:transparent}article,aside,details,figcaption,figure,footer,header,hgroup,nav,section{display:block}html{font-size:16px;line-height:24px;width:100%;height:100%;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;overflow-y:scroll;overflow-x:hidden}img{vertical-align:middle;max-width:100%;height:auto;border:0;-ms-interpolation-mode:bicubic}body{min-height:100%;-webkit-font-smoothing:subpixel-antialiased}.clearfix{clear:both;zoom:1}.clearfix:before,.clearfix:after{content:&quot;\\0020&quot;;display:block;height:0;visibility:hidden}.clearfix:after{clear:both}\r\n\r\n    </style>\r\n    <style>\r\n  .plain.error-page-wrapper {\r\n    font-family: 'Source Sans Pro', sans-serif;\r\n    background-color:#6355bc;\r\n    padding:0 5%;\r\n    position:relative;\r\n  }\r\n\r\n  .plain.error-page-wrapper .content-container {\r\n    -webkit-transition: left .5s ease-out, opacity .5s ease-out;\r\n    -moz-transition: left .5s ease-out, opacity .5s ease-out;\r\n    -ms-transition: left .5s ease-out, opacity .5s ease-out;\r\n    -o-transition: left .5s ease-out, opacity .5s ease-out;\r\n    transition: left .5s ease-out, opacity .5s ease-out;\r\n    max-width:400px;\r\n    position:relative;\r\n    left:-30px;\r\n    opacity:0;\r\n  }\r\n\r\n  .plain.error-page-wrapper .content-container.in {\r\n    left: 0px;\r\n    opacity:1;\r\n  }\r\n\r\n  .plain.error-page-wrapper .head-line {\r\n    transition: color .2s linear;\r\n    font-size:48px;\r\n    line-height:60px;\r\n    color:rgba(255,255,255,.2);\r\n    letter-spacing: -1px;\r\n    margin-bottom: 5px;\r\n  }\r\n  .plain.error-page-wrapper .subheader {\r\n    transition: color .2s linear;\r\n    font-size:36px;\r\n    line-height:46px;\r\n    color:#fff;\r\n  }\r\n  .plain.error-page-wrapper hr {\r\n    height:1px;\r\n    background-color: rgba(255,255,255,.2);\r\n    border:none;\r\n    width:250px;\r\n    margin:35px 0;\r\n  }\r\n  .plain.error-page-wrapper .context {\r\n    transition: color .2s linear;\r\n    font-size:18px;\r\n    line-height:27px;\r\n    color:#fff;\r\n  }\r\n  .plain.error-page-wrapper .context p {\r\n    margin:0;\r\n  }\r\n  .plain.error-page-wrapper .context p:nth-child(n+2) {\r\n    margin-top:12px;\r\n  }\r\n  .plain.error-page-wrapper .buttons-container {\r\n    margin-top: 45px;\r\n    overflow: hidden;\r\n  }\r\n  .plain.error-page-wrapper .buttons-container a {\r\n    transition: color .2s linear, border-color .2s linear;\r\n    font-size:14px;\r\n    text-transform: uppercase;\r\n    text-decoration: none;\r\n    color:#fff;\r\n    border:2px solid white;\r\n    border-radius: 99px;\r\n    padding:8px 30px 9px;\r\n    display: inline-block;\r\n    float:left;\r\n  }\r\n  .plain.error-page-wrapper .buttons-container a:hover {\r\n    background-color:rgba(255,255,255,.05);\r\n  }\r\n  .plain.error-page-wrapper .buttons-container a:first-child {\r\n    margin-right:25px;\r\n  }\r\n\r\n  @media screen and (max-width: 485px) {\r\n    .plain.error-page-wrapper .header {\r\n      font-size:36px;\r\n     }\r\n    .plain.error-page-wrapper .subheader {\r\n      font-size:27px;\r\n      line-height:38px;\r\n     }\r\n    .plain.error-page-wrapper hr {\r\n      width:185px;\r\n      margin:25px 0;\r\n     }\r\n\r\n    .plain.error-page-wrapper .context {\r\n      font-size:16px;\r\n      line-height: 24px;\r\n     }\r\n    .plain.error-page-wrapper .buttons-container {\r\n      margin-top:35px;\r\n    }\r\n\r\n    .plain.error-page-wrapper .buttons-container a {\r\n      font-size:13px;\r\n      padding:8px 0 7px;\r\n      width:45%;\r\n      text-align: center;\r\n    }\r\n    .plain.error-page-wrapper .buttons-container a:first-child {\r\n      margin-right:10%;\r\n    }\r\n  }\r\n    .background-color {\r\n      background-color: rgba(54, 54, 54, 1) !important;\r\n    }\r\n\r\n\r\n    .primary-text-color {\r\n      color: #FFFFFF !important;\r\n    }\r\n\r\n    .secondary-text-color {\r\n      color: rgba(255, 174, 23, 1) !important;\r\n    }\r\n\r\n    .sign-text-color {\r\n      color: #FFBA00 !important;\r\n    }\r\n\r\n    .sign-frame-color {\r\n      color: #343C3F;\r\n    }\r\n\r\n    .pane {\r\n      background-color: #FFFFFF !important;\r\n    }\r\n\r\n    .border-button {\t  display: block;\r\n\t  margin-bottom: 8px;\r\n      color: rgba(255, 225, 0, 1) !important;\r\n      border-color: rgba(255, 225, 0, 1) !important;\r\n    }\r\n    .button {\r\n      background-color: rgba(255, 225, 0, 1) !important;\r\n      color: #FFFFFF !important;\r\n    }\r\n\r\n    .shadow {\r\n      box-shadow: 0 0 60px #000000;\r\n    }\r\n\r\n</style>\r\n  <title>503 Service Unavailable ({TITLE})</title>\r\n  </head>\r\n  <body class=\"plain error-page-wrapper background-color background-image\">\r\n    <div class=\"content-container\">\r\n\t<div class=\"head-line secondary-text-color\">\r\n\t\t503 Service Unavailable\r\n\t</div>\r\n\t<div class=\"subheader primary-text-color\">\r\n\t\tThe service is unavailable <br>\r\n\t\tat this time. Please try <br>\r\n\t\tagain later.\r\n\t</div>\r\n\t<hr>\r\n\t<div class=\"clearfix\"></div>\r\n\t<div class=\"context primary-text-color\">\r\n\t\t<!-- doesn't use context_content because it's ALWAYS the same thing -->\r\n    <p>\r\n      If you feel this is in error, please contact the site administrator.\r\n    </p>\r\n\t</div>\r\n\t<div class=\"buttons-container\">\r\n\t\t{ADD_BUTTONS}\r\n\t\t<a class=\"border-button\" href=\"https://www.spigotmc.org/resources/spigot-http-server-beta.37999/\" target=\"_blank\">Spigot HTTP Server Project</a>\r\n\t</div>\r\n</div>\r\n\r\n    <script>\r\n      function ErrorPage(e,t,n){this.$container=$(e),this.$contentContainer=this.$container.find(n==\"sign\"?\".sign-container\":\".content-container\"),this.pageType=t,this.templateName=n}ErrorPage.prototype.centerContent=function(){var e=this.$container.outerHeight(),t=this.$contentContainer.outerHeight(),n=(e-t)/2,r=this.templateName==\"sign\"?-100:0;this.$contentContainer.css(\"top\",n+r)},ErrorPage.prototype.initialize=function(){var e=this;this.centerContent(),this.$container.on(\"resize\",function(t){t.preventDefault(),t.stopPropagation(),e.centerContent()}),this.templateName==\"plain\"&&window.setTimeout(function(){e.$contentContainer.addClass(\"in\")},500),this.templateName==\"sign\"&&$(\".sign-container\").animate({textIndent:0},{step:function(e){$(this).css({transform:\"rotate(\"+e+\"deg)\",\"transform-origin\":\"top center\"})},duration:1e3,easing:\"easeOutBounce\"})},ErrorPage.prototype.createTimeRangeTag=function(e,t){return\"<time utime=\"+e+' simple_format=\"MMM DD, YYYY HH:mm\">'+e+\"</time> - <time utime=\"+t+' simple_format=\"MMM DD, YYYY HH:mm\">'+t+\"</time>.\"},ErrorPage.prototype.handleStatusFetchSuccess=function(e,t){if(e==\"503\")$(\"#replace-with-fetched-data\").html(t.status.description);else if(!t.scheduled_maintenances.length)$(\"#replace-with-fetched-data\").html(\"<em>(there are no active scheduled maintenances)</em>\");else{var n=t.scheduled_maintenances[0];$(\"#replace-with-fetched-data\").html(this.createTimeRangeTag(n.scheduled_for,n.scheduled_until)),$.fn.localizeTime()}},ErrorPage.prototype.handleStatusFetchFail=function(e){$(\"#replace-with-fetched-data\").html(\"<em>(enter a valid StatusPage.io url)</em>\")},ErrorPage.prototype.fetchStatus=function(e,t){if(!e||!t||t==\"503\")return;var n=\"\",r=this;t==\"503\"?n=e+\"/api/v2/status.json\":n=e+\"/api/v2/scheduled-maintenances/active.json\",$.ajax({type:\"GET\",url:n}).success(function(e,n){r.handleStatusFetchSuccess(t,e)}).fail(function(e,n){r.handleStatusFetchFail(t)})};\r\n      var ep = new ErrorPage('body', \"503\", \"plain\");\r\n      ep.initialize();\r\n\r\n      // hack to make sure content stays centered >_<\r\n      $(window).on('resize', function() {\r\n        $('body').trigger('resize')\r\n      });\r\n\r\n    </script>\r\n\r\n    \r\n  </body>\r\n</html>\r\n";
                StringBuilder buttons = new StringBuilder();
                if (HttpServerHandler.isValidURL(HttpServerHandler.plugin.getConfig().getString("server-address"))) {
                    buttons.append(ErrorPages.createButton(HttpServerHandler
                            .plugin.getConfig().getString("server-name"), HttpServerHandler.plugin.getConfig().getString("server-address")));
                }

                if (buttons.toString().length() > 0) {
                    response = response.replace("{ADD_BUTTONS}", buttons.toString());
                } else {
                    response = response.replace("{ADD_BUTTONS}", "");
                }
                response = response.replace("{TITLE}", HttpServerHandler.plugin.getConfig().getString("server-name"));
                t.sendResponseHeaders(503, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
                HttpServerHandler.plugin.getLogger().log(Level.WARNING, "Connections: " + HttpServerHandler
                        .connections + "/" + HttpServerHandler.plugin.getConfig().getInt("max-connections") +
                        " | Server is overloaded and dropping connections!");

                HttpServerHandler.connections = HttpServerHandler.connections - 1;

                return;
            }

            URI uri = t.getRequestURI();
            File file = (new File(HttpServerHandler.plugin.getConfig().getString("server-path") + File.separator + uri.getPath())).getCanonicalFile();
            File filePHP = (new File(HttpServerHandler.plugin.getConfig().getString("server-path") + File.separator + uri.getPath())).getCanonicalFile();

            String log = "[" + requestType.toUpperCase() + "] Request from: " + t.getRemoteAddress() + " to: " + t.getRequestURI() + "\n";

            File lf = new File(HttpServerHandler.plugin.getConfig().getString("server-path").replace("public_html", "") + "/access.logs");
            if (!lf.exists()) {
                lf.mkdirs();
            }
            try {
                Exception exception2, exception1 = null;


            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!file.getPath().startsWith(HttpServerHandler.plugin.getConfig().getString("server-path")) || file.getPath().contains("..")) {


                Headers headers = t.getResponseHeaders();
                headers.add("Server", serverHeader);
                headers.add("Date", serverTime);
                headers.add("Cache-Control", "Cache-Control: max-age=0, s-maxage=7200, must-revalidate");
                headers.add("Expires", HttpServerHandler.getExpiresTime(true));
                headers.add("Connection", "close");

                String response = "<!DOCTYPE html>\r\n<html>\r\n  <head>\r\n    <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js\"></script>\r\n    <script src=\"https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js\"></script>\r\n    <link href=\"https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,600\" media=\"screen\" rel=\"stylesheet\" />\r\n    <link href=\"https://maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css\" media=\"screen\" rel=\"stylesheet\" />\r\n\r\n\r\n    <style>\r\n      *{-moz-box-sizing:border-box;-webkit-box-sizing:border-box;box-sizing:border-box}html,body,div,span,object,iframe,h1,h2,h3,h4,h5,h6,p,blockquote,pre,abbr,address,cite,code,del,dfn,em,img,ins,kbd,q,samp,small,strong,sub,sup,var,b,i,dl,dt,dd,ol,ul,li,fieldset,form,label,legend,caption,article,aside,canvas,details,figcaption,figure,footer,header,hgroup,menu,nav,section,summary,time,mark,audio,video{margin:0;padding:0;border:0;outline:0;vertical-align:baseline;background:transparent}article,aside,details,figcaption,figure,footer,header,hgroup,nav,section{display:block}html{font-size:16px;line-height:24px;width:100%;height:100%;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;overflow-y:scroll;overflow-x:hidden}img{vertical-align:middle;max-width:100%;height:auto;border:0;-ms-interpolation-mode:bicubic}body{min-height:100%;-webkit-font-smoothing:subpixel-antialiased}.clearfix{clear:both;zoom:1}.clearfix:before,.clearfix:after{content:&quot;\\0020&quot;;display:block;height:0;visibility:hidden}.clearfix:after{clear:both}\r\n\r\n    </style>\r\n    <style>\r\n  .plain.error-page-wrapper {\r\n    font-family: 'Source Sans Pro', sans-serif;\r\n    background-color:#6355bc;\r\n    padding:0 5%;\r\n    position:relative;\r\n  }\r\n\r\n  .plain.error-page-wrapper .content-container {\r\n    -webkit-transition: left .5s ease-out, opacity .5s ease-out;\r\n    -moz-transition: left .5s ease-out, opacity .5s ease-out;\r\n    -ms-transition: left .5s ease-out, opacity .5s ease-out;\r\n    -o-transition: left .5s ease-out, opacity .5s ease-out;\r\n    transition: left .5s ease-out, opacity .5s ease-out;\r\n    max-width:400px;\r\n    position:relative;\r\n    left:-30px;\r\n    opacity:0;\r\n  }\r\n\r\n  .plain.error-page-wrapper .content-container.in {\r\n    left: 0px;\r\n    opacity:1;\r\n  }\r\n\r\n  .plain.error-page-wrapper .head-line {\r\n    transition: color .2s linear;\r\n    font-size:48px;\r\n    line-height:60px;\r\n    color:rgba(255,255,255,.2);\r\n    letter-spacing: -1px;\r\n    margin-bottom: 5px;\r\n  }\r\n  .plain.error-page-wrapper .subheader {\r\n    transition: color .2s linear;\r\n    font-size:36px;\r\n    line-height:46px;\r\n    color:#fff;\r\n  }\r\n  .plain.error-page-wrapper hr {\r\n    height:1px;\r\n    background-color: rgba(255,255,255,.2);\r\n    border:none;\r\n    width:250px;\r\n    margin:35px 0;\r\n  }\r\n  .plain.error-page-wrapper .context {\r\n    transition: color .2s linear;\r\n    font-size:18px;\r\n    line-height:27px;\r\n    color:#fff;\r\n  }\r\n  .plain.error-page-wrapper .context p {\r\n    margin:0;\r\n  }\r\n  .plain.error-page-wrapper .context p:nth-child(n+2) {\r\n    margin-top:12px;\r\n  }\r\n  .plain.error-page-wrapper .buttons-container {\r\n    margin-top: 45px;\r\n    overflow: hidden;\r\n  }\r\n  .plain.error-page-wrapper .buttons-container a {\r\n    transition: color .2s linear, border-color .2s linear;\r\n    font-size:14px;\r\n    text-transform: uppercase;\r\n    text-decoration: none;\r\n    color:#fff;\r\n    border:2px solid white;\r\n    border-radius: 99px;\r\n    padding:8px 30px 9px;\r\n    display: inline-block;\r\n    float:left;\r\n  }\r\n  .plain.error-page-wrapper .buttons-container a:hover {\r\n    background-color:rgba(255,255,255,.05);\r\n  }\r\n  .plain.error-page-wrapper .buttons-container a:first-child {\r\n    margin-right:25px;\r\n  }\r\n\r\n  @media screen and (max-width: 485px) {\r\n    .plain.error-page-wrapper .header {\r\n      font-size:36px;\r\n     }\r\n    .plain.error-page-wrapper .subheader {\r\n      font-size:27px;\r\n      line-height:38px;\r\n     }\r\n    .plain.error-page-wrapper hr {\r\n      width:185px;\r\n      margin:25px 0;\r\n     }\r\n\r\n    .plain.error-page-wrapper .context {\r\n      font-size:16px;\r\n      line-height: 24px;\r\n     }\r\n    .plain.error-page-wrapper .buttons-container {\r\n      margin-top:35px;\r\n    }\r\n\r\n    .plain.error-page-wrapper .buttons-container a {\r\n      font-size:13px;\r\n      padding:8px 0 7px;\r\n      width:45%;\r\n      text-align: center;\r\n    }\r\n    .plain.error-page-wrapper .buttons-container a:first-child {\r\n      margin-right:10%;\r\n    }\r\n  }\r\n    .background-color {\r\n      background-color: rgba(54, 54, 54, 1) !important;\r\n    }\r\n\r\n\r\n    .primary-text-color {\r\n      color: #FFFFFF !important;\r\n    }\r\n\r\n    .secondary-text-color {\r\n      color: rgba(255, 174, 23, 1) !important;\r\n    }\r\n\r\n    .sign-text-color {\r\n      color: #FFBA00 !important;\r\n    }\r\n\r\n    .sign-frame-color {\r\n      color: #343C3F;\r\n    }\r\n\r\n    .pane {\r\n      background-color: #FFFFFF !important;\r\n    }\r\n\r\n    .border-button {\t  display: block;\r\n\t  margin-bottom: 8px;\r\n      color: rgba(255, 225, 0, 1) !important;\r\n      border-color: rgba(255, 225, 0, 1) !important;\r\n    }\r\n    .button {\r\n      background-color: rgba(255, 225, 0, 1) !important;\r\n      color: #FFFFFF !important;\r\n    }\r\n\r\n    .shadow {\r\n      box-shadow: 0 0 60px #000000;\r\n    }\r\n\r\n</style>\r\n  <title>403 Access Forbidden ({TITLE})</title>\r\n  </head>\r\n  <body class=\"plain error-page-wrapper background-color background-image\">\r\n    <div class=\"content-container\">\r\n\t<div class=\"head-line secondary-text-color\">\r\n\t\t403 Access Forbidden\r\n\t</div>\r\n\t<div class=\"subheader primary-text-color\">\r\n\t\tYou are not allowed <br>\r\n\t\taccess to this content.\r\n\t</div>\r\n\t<hr>\r\n\t<div class=\"clearfix\"></div>\r\n\t<div class=\"context primary-text-color\">\r\n\t\t<!-- doesn't use context_content because it's ALWAYS the same thing -->\r\n    <p>\r\n      If you feel this is in error, please contact the site administrator.\r\n    </p>\r\n\t</div>\r\n\t<div class=\"buttons-container\">\r\n\t\t{ADD_BUTTONS}\r\n\t\t<a class=\"border-button\" href=\"https://www.spigotmc.org/resources/spigot-http-server-beta.37999/\" target=\"_blank\">Spigot HTTP Server Project</a>\r\n\t</div>\r\n</div>\r\n\r\n    <script>\r\n      function ErrorPage(e,t,n){this.$container=$(e),this.$contentContainer=this.$container.find(n==\"sign\"?\".sign-container\":\".content-container\"),this.pageType=t,this.templateName=n}ErrorPage.prototype.centerContent=function(){var e=this.$container.outerHeight(),t=this.$contentContainer.outerHeight(),n=(e-t)/2,r=this.templateName==\"sign\"?-100:0;this.$contentContainer.css(\"top\",n+r)},ErrorPage.prototype.initialize=function(){var e=this;this.centerContent(),this.$container.on(\"resize\",function(t){t.preventDefault(),t.stopPropagation(),e.centerContent()}),this.templateName==\"plain\"&&window.setTimeout(function(){e.$contentContainer.addClass(\"in\")},500),this.templateName==\"sign\"&&$(\".sign-container\").animate({textIndent:0},{step:function(e){$(this).css({transform:\"rotate(\"+e+\"deg)\",\"transform-origin\":\"top center\"})},duration:1e3,easing:\"easeOutBounce\"})},ErrorPage.prototype.createTimeRangeTag=function(e,t){return\"<time utime=\"+e+' simple_format=\"MMM DD, YYYY HH:mm\">'+e+\"</time> - <time utime=\"+t+' simple_format=\"MMM DD, YYYY HH:mm\">'+t+\"</time>.\"},ErrorPage.prototype.handleStatusFetchSuccess=function(e,t){if(e==\"503\")$(\"#replace-with-fetched-data\").html(t.status.description);else if(!t.scheduled_maintenances.length)$(\"#replace-with-fetched-data\").html(\"<em>(there are no active scheduled maintenances)</em>\");else{var n=t.scheduled_maintenances[0];$(\"#replace-with-fetched-data\").html(this.createTimeRangeTag(n.scheduled_for,n.scheduled_until)),$.fn.localizeTime()}},ErrorPage.prototype.handleStatusFetchFail=function(e){$(\"#replace-with-fetched-data\").html(\"<em>(enter a valid StatusPage.io url)</em>\")},ErrorPage.prototype.fetchStatus=function(e,t){if(!e||!t||t==\"403\")return;var n=\"\",r=this;t==\"503\"?n=e+\"/api/v2/status.json\":n=e+\"/api/v2/scheduled-maintenances/active.json\",$.ajax({type:\"GET\",url:n}).success(function(e,n){r.handleStatusFetchSuccess(t,e)}).fail(function(e,n){r.handleStatusFetchFail(t)})};\r\n      var ep = new ErrorPage('body', \"403\", \"plain\");\r\n      ep.initialize();\r\n\r\n      // hack to make sure content stays centered >_<\r\n      $(window).on('resize', function() {\r\n        $('body').trigger('resize')\r\n      });\r\n\r\n    </script>\r\n\r\n    \r\n  </body>\r\n</html>\r\n";
                StringBuilder buttons = new StringBuilder();
                if (HttpServerHandler.isValidURL(HttpServerHandler.plugin.getConfig().getString("server-address"))) {
                    buttons.append(ErrorPages.createButton(HttpServerHandler
                            .plugin.getConfig().getString("server-name"), HttpServerHandler.plugin.getConfig().getString("server-address")));
                }

                if (buttons.toString().length() > 0) {
                    response = response.replace("{ADD_BUTTONS}", buttons.toString());
                } else {
                    response = response.replace("{ADD_BUTTONS}", "");
                }
                response = response.replace("{TITLE}", HttpServerHandler.plugin.getConfig().getString("server-name"));
                t.sendResponseHeaders(403, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();

            } else if (file.isDirectory()) {

                String uriPath = "";
                if (!uri.getPath().isEmpty()) {
                    uriPath = uri.getPath();
                    if (!uriPath.substring(uriPath.length() - 1).equalsIgnoreCase("/")) {
                        uriPath = uriPath + File.separator;
                    }
                }

                //file = (new File(HttpServerHandler.plugin.getConfig().getString("server-path") +
                //        File.separator + uriPath + "index.html")).getCanonicalFile();

                File existTest = new File(plugin.getConfig().getString("server-path") +
                        File.separator + uriPath + "index.html");
                File existTest1 = new File(plugin.getConfig().getString("server-path") +
                        File.separator + uriPath + "index.php");

                if (existTest.exists() && !plugin.getConfig().getBoolean("php-index")) {
                    file = (new File(HttpServerHandler.plugin.getConfig().getString("server-path") +
                            File.separator + uriPath + "index.html")).getCanonicalFile();
                }else if (existTest1.exists() && plugin.getConfig().getBoolean("php-index")){
                    file = (new File(HttpServerHandler.plugin.getConfig().getString("server-path") +
                            File.separator + uriPath + "index.php")).getCanonicalFile();
                }



                if (HttpServerHandler.plugin.getConfig().getBoolean("php-gateway-enabled")) {
                    filePHP = (new File(HttpServerHandler.plugin.getConfig().getString("server-path") +
                            File.separator + uriPath + "index.php")).getCanonicalFile();
                }

                if (filePHP.isFile()) {

                    Headers headers = t.getResponseHeaders();
                    headers.add("Server", serverHeader);
                    headers.add("Date", serverTime);
                    headers.add("Expires", HttpServerHandler.getExpiresTime(true));
                    headers.add("Connection", "close");

                    HashMap<String, String> headersPHP = HttpServerHandler.extractPHPHeaders(file.getPath());

                    if (headersPHP != null && headersPHP.size() > 0 && ((headersPHP.containsKey("location") && headersPHP.get("location") != null) ||
                            !(headersPHP.get("location")).equals("null"))) {
                        HttpServerHandler.sendRedirect(headersPHP.get("location"), t);
                    }

                    if (headersPHP != null && headersPHP.size() > 0) {
                        if (headersPHP.size() > 0 && !headersPHP.containsKey("content-type")) {
                            headers.add("Content-Type", "text/html; charset=utf-8");
                        }

                        if (headersPHP.size() > 0) {
                            for (String type : headersPHP.keySet()) {
                                headers.add(type, headersPHP.get(type));
                            }
                        }
                    }


                    String response = HttpServerHandler.phpGatewayBuffered(filePHP.getPath(), uri, t, true);


                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();

                } else if (file.isFile()) {

                    Headers headers = t.getResponseHeaders();
                    headers.add("Server", serverHeader);
                    headers.add("Date", serverTime);
                    headers.add("Expires", HttpServerHandler.getExpiresTime(false));
                    headers.add("Connection", "close");
                    headers.add("Content-Type", "text/html; charset=utf-8");

                    if (HttpServerHandler.plugin.getConfig().getBoolean("shstags-enabled")) {
                        StringBuilder streamBuilder = new StringBuilder();

                        try {
                            FileInputStream fs = new FileInputStream(file);
                            BufferedReader input = new BufferedReader(new InputStreamReader(fs));
                            String line;
                            while ((line = input.readLine()) != null) {
                                streamBuilder.append(line).append("\n");
                            }
                            input.close();
                            fs.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        InputStream nis = new ByteArrayInputStream(streamBuilder.toString().getBytes(StandardCharsets.UTF_8));


                        t.sendResponseHeaders(200, 0L);
                        OutputStream os = t.getResponseBody();
                        byte[] buffer = new byte[65536];
                        int count = 0;
                        while ((count = nis.read(buffer)) >= 0) {
                            os.write(buffer, 0, count);
                        }
                        os.close();
                        nis.close();

                    } else {
                        InputStream nis = new FileInputStream(file);

                        t.sendResponseHeaders(200, 0L);
                        OutputStream os = t.getResponseBody();
                        byte[] buffer = new byte[65536];
                        int count = 0;
                        while ((count = nis.read(buffer)) >= 0) {
                            os.write(buffer, 0, count);
                        }
                        os.close();
                        nis.close();
                    }
                } else {
                    Headers headers = t.getResponseHeaders();
                    headers.add("Server", serverHeader);
                    headers.add("Date", serverTime);
                    headers.add("Expires", HttpServerHandler.getExpiresTime(true));
                    headers.add("Connection", "close");

                    StringBuilder streamBuilder = new StringBuilder();

                    String file_error = HttpServerHandler.plugin.getConfig().getString("server-path") +
                            File.separator + plugin.getConfig().getString("404-error");

                    try {
                        FileInputStream fs = new FileInputStream(file_error);
                        BufferedReader input = new BufferedReader(new InputStreamReader(fs));
                        String line;
                        while ((line = input.readLine()) != null) {
                            streamBuilder.append(line).append("\n");
                        }
                        input.close();
                        fs.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    InputStream nis = new ByteArrayInputStream(streamBuilder.toString().getBytes(StandardCharsets.UTF_8));

                    t.sendResponseHeaders(404, 0L);
                    OutputStream os = t.getResponseBody();
                    byte[] buffer = new byte[65536];
                    int count = 0;
                    while ((count = nis.read(buffer)) >= 0) {
                        os.write(buffer, 0, count);
                    }
                    os.close();
                    nis.close();

                }

            } else if (!file.isFile()) {


                Headers headers = t.getResponseHeaders();
                headers.add("Server", serverHeader);
                headers.add("Date", serverTime);
                headers.add("Expires", HttpServerHandler.getExpiresTime(true));
                headers.add("Connection", "close");

                StringBuilder streamBuilder = new StringBuilder();

                String file_error = HttpServerHandler.plugin.getConfig().getString("server-path") +
                        File.separator + plugin.getConfig().getString("404-error");

                try {
                    FileInputStream fs = new FileInputStream(file_error);
                    BufferedReader input = new BufferedReader(new InputStreamReader(fs));
                    String line;
                    while ((line = input.readLine()) != null) {
                        streamBuilder.append(line).append("\n");
                    }
                    input.close();
                    fs.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                InputStream nis = new ByteArrayInputStream(streamBuilder.toString().getBytes(StandardCharsets.UTF_8));

                t.sendResponseHeaders(404,0L);
                OutputStream os = t.getResponseBody();
                byte[] buffer = new byte[65536];
                int count = 0;
                while ((count = nis.read(buffer)) >= 0) {
                    os.write(buffer, 0, count);
                }
                os.close();
                nis.close();

            } else if (HttpServerHandler.getExtension(file.getPath()).equals("json")) {


                Headers headers = t.getResponseHeaders();

                headers.add("Server", serverHeader);
                headers.add("Date", serverTime);
                headers.add("Expires", HttpServerHandler.getExpiresTime(true));
                headers.add("Connection", "close");
                headers.add("Content-Type", "application/json");

                if (HttpServerHandler.plugin.getConfig().getBoolean("shstags-enabled")) {
                    StringBuilder streamBuilder = new StringBuilder();


                    try {
                        FileInputStream fs = new FileInputStream(file);
                        BufferedReader input = new BufferedReader(new InputStreamReader(fs));
                        String line;
                        while ((line = input.readLine()) != null) {
                            streamBuilder.append(line).append("\n");
                        }
                        input.close();
                        fs.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    InputStream nis = new ByteArrayInputStream(streamBuilder.toString().getBytes(StandardCharsets.UTF_8));


                    t.sendResponseHeaders(200, 0L);
                    OutputStream os = t.getResponseBody();
                    byte[] buffer = new byte[65536];
                    int count = 0;
                    while ((count = nis.read(buffer)) >= 0) {
                        os.write(buffer, 0, count);
                    }
                    os.close();
                    nis.close();

                } else {
                    InputStream nis = new FileInputStream(file);


                    t.sendResponseHeaders(200, 0L);
                    OutputStream os = t.getResponseBody();
                    byte[] buffer = new byte[65536];
                    int count = 0;
                    while ((count = nis.read(buffer)) >= 0) {
                        os.write(buffer, 0, count);
                    }
                    os.close();
                    nis.close();
                }

            } else if (HttpServerHandler.plugin.getConfig().getBoolean("php-gateway-enabled") && HttpServerHandler.getExtension(file.getPath()).equals("php")) {


                Headers headers = t.getResponseHeaders();

                headers.add("Server", serverHeader);
                headers.add("Date", serverTime);
                headers.add("Expires", HttpServerHandler.getExpiresTime(true));
                headers.add("Connection", "close");


                HashMap<String, String> headersPHP = HttpServerHandler.extractPHPHeaders(file.getPath());

                List<String> cookies = HttpServerHandler.extractPHPSetCookies(file.getPath());


                if (headersPHP != null && headersPHP.size() > 0 && ((
                        headersPHP.containsKey("location") && headersPHP.get("location") != null) ||
                        !headersPHP.get("location").equals("null"))) {
                    HttpServerHandler.sendRedirect(headersPHP.get("location"), t);
                }


                if (headersPHP != null && headersPHP.size() > 0) {
                    if (headersPHP.size() > 0 && !headersPHP.containsKey("content-type")) {
                        headers.add("Content-Type", "text/html; charset=utf-8");
                    }

                    if (headersPHP.size() > 0) {
                        for (String type : headersPHP.keySet()) {
                            headers.add(type, headersPHP.get(type));
                        }
                    }
                }


                String response = HttpServerHandler.phpGatewayBuffered(filePHP.getPath(), uri, t, false);


                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();

            } else {


                Headers headers = t.getResponseHeaders();
                headers.add("Server", serverHeader);
                headers.add("Date", serverTime);
                headers.add("Expires", HttpServerHandler.getExpiresTime(false));
                headers.add("Connection", "close");


                if (HttpServerHandler.getMimeType(file.getPath()) != null) {
                    headers.add("Content-Type", HttpServerHandler.getMimeType(file.getPath()));
                }

                if ((HttpServerHandler.plugin.getConfig().getBoolean("shstags-enabled") && HttpServerHandler
                        .getExtension(file.getPath()).equals("html")) || HttpServerHandler
                        .getExtension(file.getPath()).equals("htm")) {

                    StringBuilder streamBuilder = new StringBuilder();

                    try {
                        FileInputStream fs = new FileInputStream(file);
                        BufferedReader input = new BufferedReader(new InputStreamReader(fs));
                        String line;
                        while ((line = input.readLine()) != null && line != null) {
                            streamBuilder.append(line).append("\n");
                        }
                        input.close();
                        fs.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    System.out.println(streamBuilder);

                    InputStream nis = new ByteArrayInputStream(streamBuilder.toString().getBytes(StandardCharsets.UTF_8));

                    t.sendResponseHeaders(200, 0L);
                    OutputStream os = t.getResponseBody();
                    byte[] buffer = new byte[65536];
                    int count = 0;
                    while ((count = nis.read(buffer)) >= 0) {
                        os.write(buffer, 0, count);
                    }
                    os.close();
                    nis.close();

                } else {

                    InputStream nis = new FileInputStream(file);


                    t.sendResponseHeaders(200, 0L);
                    OutputStream os = t.getResponseBody();
                    byte[] buffer = new byte[65536];
                    int count = 0;
                    while ((count = nis.read(buffer)) >= 0) {
                        os.write(buffer, 0, count);
                    }
                    os.close();
                    nis.close();
                }
            }


            HttpServerHandler.connections = HttpServerHandler.connections - 1;
        }
    }

    private static HttpServer server = null;
    private static final FileNameMap fileNameMap = URLConnection.getFileNameMap();
    private static int connections = 0;
    public static final Escaper SHELL_ESCAPE;

    private static String phpGatewayBuffered(String fileSource, URI uri, HttpExchange t, boolean index) {
        StringBuilder outputBuffered = new StringBuilder();

        File file = new File(fileSource);

        String req = t.getRequestMethod();
        String path = uri.getPath();
        String host = uri.getHost();
        String rawPath = uri.getRawPath();
        String query = "";


        String[] tokens = uri.toString().split("\\?", 2);
        if (tokens.length == 2) {
            query = SHELL_ESCAPE.escape(tokens[1]);
        }


        StringBuilder args = new StringBuilder();


        StringBuilder arbCode = new StringBuilder();


        StringBuilder shsGlobals = new StringBuilder();
        shsGlobals.append("$_SHS['SERVER_NAME']='").append(Bukkit.getServer().getName()).append("'; ");
        shsGlobals.append("$_SHS['SERVER_VERSION']='").append(Bukkit.getServer().getBukkitVersion()).append("'; ");
        shsGlobals.append("$_SHS['SERVER_START_TIMESTAMP']='").append(WebServer.startTime).append("'; ");
        shsGlobals.append("$_SHS['SERVER_IP']='").append(Bukkit.getServer().getIp()).append("'; ");
        shsGlobals.append("$_SHS['SERVER_PORT']='").append(Bukkit.getServer().getPort()).append("'; ");
        shsGlobals.append("$_SHS['SERVER_MAX_PLAYERS']='").append(Bukkit.getServer().getMaxPlayers()).append("'; ");
        shsGlobals.append("$_SHS['SERVER_MOTD']='").append(ChatColor.stripColor(Bukkit.getServer().getMotd())).append("'; ");
        shsGlobals.append("$_SHS['BANNED_PLAYERS']=").append(PHPGetBanList()).append(" ");
        shsGlobals.append("$_SHS['BANNED_IP']=").append(PHPGetBanIPList()).append(" ");


        StringBuilder serverGlobals = new StringBuilder();
        serverGlobals.append("$_SERVER['SERVER_SOFTWARE']='Spigot HTTP Server version 0.0.5 (beta)'; ");
        serverGlobals.append("$_SERVER['HTTP_HOST']='").append(host).append("'; ");
        serverGlobals.append("$_SERVER['PHP_SELF']='").append(path).append("'; ");
        serverGlobals.append("$_SERVER['SCRIPT_NAME']='").append(rawPath).append("'; ");
        if (index) {
            serverGlobals.append("$_SERVER['SCRIPT_FILENAME']='").append(file.getPath()).append("index.php'; ");
        } else {
            serverGlobals.append("$_SERVER['SCRIPT_FILENAME']='").append(file.getPath()).append("'; ");
        }
        serverGlobals.append("$_SERVER['REQUEST_URI']='").append(uri).append("'; ");
        serverGlobals.append("$_SERVER['REQUEST_METHOD']='").append(req).append("'; ");
        serverGlobals.append("$_SERVER['QUERY_STRING']='").append(query).append("'; ");
        serverGlobals.append("$_SERVER['SERVER_ADDR']='").append(plugin.getConfig().getString("server-ip")).append("'; ");
        serverGlobals.append("$_SERVER['SERVER_PORT']='").append(plugin.getConfig().getInt("server-port")).append("'; ");
        serverGlobals.append("$_SERVER['DOCUMENT_ROOT']='").append(plugin.getConfig().getString("server-path")).append("'; ");
        serverGlobals.append("$_SERVER['REMOTE_ADDR']='").append(t.getRemoteAddress().toString().substring(1).split(":")[0]).append("'; ");
        serverGlobals.append("unset($_SERVER['argv']); ");

        if (!query.isEmpty()) {
            try {
                Map<String, List<String>> queries = splitQuery(SHELL_ESCAPE.escape(uri.toString()));
                int cur = 1;
                for (String key : queries.keySet()) {
                    args.append(key).append("=").append(queries.get(key).get(0));
                    if (cur < queries.size()) {
                        args.append("&");
                    }
                    cur++;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if (file.isFile()) {

            try {
                Process p = Runtime.getRuntime().exec(plugin.getConfig().getString("php-path") +
                        " -r \"parse_str(implode('&', array_slice($argv, 1)), $_GET); " +
                        arbCode + " " + serverGlobals + " $_SHS = array(); " + shsGlobals + " " + PHPGetPlayersOnline() +
                        " require_once('" + file.getPath() + "');\" \"" + args + "\"");
                BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = input.readLine()) != null) {
                    outputBuffered.append(line).append("\n");
                }
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return outputBuffered.toString();
        }
        return null;
    }

    static {
        Escapers.Builder builder = Escapers.builder();
        builder.addEscape('\'', "'\"'\"'");
        SHELL_ESCAPE = builder.build();
    }

    public HttpServerHandler(JavaPlugin plug) {
        plugin = plug;
        if (plugin.getConfig().getBoolean("shstags-enabled")) ;
        if (plugin.getConfig().getString("server-path") == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',WebServer.prefix + " Please configurate your HTTP Server Path"));
            return;
        }
    }

    public static void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(plugin.getConfig().getString("server-ip"), plugin.getConfig().getInt("server-port")), 0);
            if (plugin.getConfig().isSet("additional-ips")) {
                List<String> additionalIps = plugin.getConfig().getStringList("additional-ips");
                for (String ip : additionalIps)
                    server.bind(new InetSocketAddress(ip, plugin.getConfig().getInt("server-port")), 0);
            }
            server.createContext("/", new ServerHandler());
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
        } catch (Exception e) {
            server = null;
            e.printStackTrace();
        }
        if (isRunning()) {
            //plugin.getLogger().log(Level.INFO, ChatColor.translateAlternateColorCodes('&',WebServer.prefix + " Server Startet on: " + plugin.getConfig().getString("server-ip") + ":" + plugin.getConfig().getInt("server-port")));
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',WebServer.prefix + " Server Startet on: " + plugin.getConfig().getString("server-ip") + ":" + plugin.getConfig().getInt("server-port")));
        } else {
            //plugin.getLogger().log(Level.WARNING, ChatColor.translateAlternateColorCodes('&',WebServer.prefix + " Failed to start HTTP Server!"));
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',WebServer.prefix + " Failed to start HTTP Server!"));
        }
    }

    public static void stop() {
        try {
            server.stop(0);
            server = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isRunning()) {
            //plugin.getLogger().log(Level.WARNING, ChatColor.translateAlternateColorCodes('&',WebServer.prefix + " Failed to stop HTTP Server!"));
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',WebServer.prefix + " Failed to stop HTTP Server!"));
        } else {
            //plugin.getLogger().log(Level.INFO, ChatColor.translateAlternateColorCodes('&',WebServer.prefix + " HTTP Server stopped."));
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',WebServer.prefix + " HTTP Server stopped."));
        }
    }

    public static boolean isRunning() {
        return server != null;
    }

    private static String getMimeType(String fileURI) {
        return fileNameMap.getContentTypeFor(fileURI);
        /*
        String type = fileNameMap.getContentTypeFor(fileURI);
        return type;
         */
    }


    private static String getExtension(String fileURI) {
        return FilenameUtils.getExtension(fileURI);
    }

    private static Map<String, List<String>> splitQuery(String query) throws UnsupportedEncodingException {
        Map<String, List<String>> query_pairs = new LinkedHashMap<>();
        String[] tokens = query.split("\\?", 2);
        if (tokens.length == 2) {
            query = tokens[1];
            String[] pairs = query.split("&");
            byte b;
            int i;
            String[] arrayOfString1;
            for (i = (arrayOfString1 = pairs).length, b = 0; b < i; ) {
                String pair = arrayOfString1[b];
                int idx = pair.indexOf("=");
                String key = (idx > 0) ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
                if (!query_pairs.containsKey(key)) {
                    query_pairs.put(key, new LinkedList<>());
                }
                String value = (idx > 0 && pair.length() > idx + 1) ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
                query_pairs.get(key).add(value);
                b++;
            }

        }
        return query_pairs;
    }

    private static String PHPGetBanList() {
        StringBuilder phpblock = new StringBuilder();
        phpblock.append("array(");
        for (BanEntry entry : Bukkit.getServer().getBanList(BanList.Type.NAME).getBanEntries()) {
            if (entry != null) {
                phpblock.append("'").append(ChatColor.stripColor(entry.getTarget())).append("'=>");
                String entryArray = "array(" +
                        "'BAN_CREATED'=>'" + entry.getCreated() + "', " +
                        "'BAN_EXPIRES'=>'" + entry.getExpiration() + "', " +
                        "'BAN_REASON'=>'" + entry.getReason() + "', " +
                        "'BAN_BY'=>'" + ChatColor.stripColor(entry.getSource()) + "'" +
                        "), ";
                phpblock.append(entryArray);
            }
        }
        phpblock.append("); ");
        return phpblock.toString();
    }

    private static String PHPGetPlayersOnline() {
        StringBuilder phpblock = new StringBuilder();
        phpblock.append("$_SHS['PLAYERS_ONLINE'] = array(");
        int onlineNum = Bukkit.getServer().getOnlinePlayers().size();
        int cur = 1;
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            phpblock.append("'").append(ChatColor.stripColor(player.getDisplayName())).append("'=>'").append(player.getUniqueId()).append("'");
            if (cur < onlineNum) {
                phpblock.append(", ");
            }
            cur++;
        }
        phpblock.append("); ");
        return phpblock.toString();
    }

    private static String PHPGetBanIPList() {
        StringBuilder phpblock = new StringBuilder();
        phpblock.append("array(");
        int banNum = Bukkit.getServer().getIPBans().size();
        int cur = 1;
        for (String ip : Bukkit.getServer().getIPBans()) {
            if (ip != null) {
                phpblock.append("'").append(ip).append("'");
                if (cur < banNum) {
                    phpblock.append(", ");
                }
            }
            cur++;
        }
        phpblock.append("); ");
        return phpblock.toString();
    }

    private static HashMap<String, String> extractPHPHeaders(String path) {
        HashMap<String, String> headers = new HashMap<>();
        if (getExtension(path).equals("php")) {
            try {
                Exception exception2, exception1 = null;


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return headers;
    }

    private static List<String> extractPHPSetCookies(String path) {
        List<String> cookies = new ArrayList<>();
        if (getExtension(path).equals("php")) {
            try {
                Exception exception2, exception1 = null;


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return cookies;
    }


    public static String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

    public static String getExpiresTime(boolean php) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        if (!php) {
            calendar.setTime(new Date());
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            calendar.add(11, 4);
        } else {
            calendar.setTime(new Date((new Date()).getTime() - 864000000L));
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            calendar.add(11, -4);
        }
        return dateFormat.format(calendar.getTime());
    }


    private static void sendRedirect(String location, HttpExchange t) throws IOException {
        Headers headers = t.getResponseHeaders();
        headers.add("Server", "Spigot HTTP Server version 0.0.5 (beta)");
        headers.add("Date", getServerTime());
        headers.add("Location", location);
        headers.add("Connection", "close");
        String response = "";
        t.sendResponseHeaders(302, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public static boolean isValidURL(String url) {
        Pattern c = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
                2);
        Matcher m = c.matcher(url);
        return m.matches();
        /*
        if (m.matches()) {
            return true;
        }
        return false;
         */
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }
}