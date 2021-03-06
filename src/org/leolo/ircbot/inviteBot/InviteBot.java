package org.leolo.ircbot.inviteBot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.pircbotx.Configuration;
import org.pircbotx.Configuration.Builder;
import org.pircbotx.PircBotX;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.cap.SASLCapHandler;
import org.pircbotx.exception.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InviteBot{
	
	public static final String BOT_VERSION = "0.1-preview";
	final static Logger logger = LoggerFactory.getLogger(InviteBot.class);
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String [] args) throws IOException{
		Config config;
		if(args.length == 0)
			config = new Config();
		else
			config = new Config(args[0]);
		Inviter inviter = new Inviter(config);
		Builder b = new Configuration.Builder()
		.setName(config.getNick()) //Nick of the bot. CHANGE IN YOUR CODE
		.setLogin(config.getIdent()) //Login part of hostmask, eg name:login@host
		.setAutoNickChange(true) //Automatically change nick when the current one is in use
		.setServer(config.getServer(), config.getPort())
		.addListener(inviter)
		.setAutoReconnect(true)
		.addListener(new Console(config,inviter));
		if(config.isSSL()){
			b = b.setSocketFactory(new UtilSSLSocketFactory().disableDiffieHellman().trustAllCertificates());
			b = b.setCapEnabled(true).addCapHandler(new SASLCapHandler(config.getNick(), config.getPassword()));
		}
		b = b.addCapHandler(new EnableCapHandler("extended-join",false));
		PircBotX myBot = new PircBotX(b.buildConfiguration());
		try {
			myBot.startBot();
		} catch (IrcException e) {
			logger.error(e.toString(), e);
			e.printStackTrace();
		}
	}
}
