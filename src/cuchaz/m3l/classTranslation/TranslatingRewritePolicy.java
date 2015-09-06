/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTranslation;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.rewrite.RewriteAppender;
import org.apache.logging.log4j.core.appender.rewrite.RewritePolicy;
import org.apache.logging.log4j.core.config.AppenderRef;

import ch.qos.logback.classic.Level;

import com.google.common.collect.Lists;

import cuchaz.m3l.Constants;
import cuchaz.m3l.Side;

public class TranslatingRewritePolicy implements RewritePolicy {
	
	private ExceptionTranslator m_translator;
	
	public TranslatingRewritePolicy() {
		m_translator = new ExceptionTranslator(Constants.getMappings(Side.Client));
	}
	
	@Override
	public LogEvent rewrite(LogEvent event) {
		
		// translate exceptions
		m_translator.translate(event.getThrown());
		
		return event;
	}

	public static void install() {
		
		// get the root logger
		org.apache.logging.log4j.core.Logger root = (org.apache.logging.log4j.core.Logger)LogManager.getRootLogger();
		
		// remove all the existing appenders (but save them for later)
		List<Appender> appenders = Lists.newArrayList();
		for (Appender appender : Lists.newArrayList(root.getAppenders().values())) {
			root.removeAppender(appender);
			appenders.add(appender);
		}
		
		// get appender refs
		AppenderRef[] refs = new AppenderRef[appenders.size()];
		for (int i=0; i<appenders.size(); i++) {
			refs[i] = AppenderRef.createAppenderRef(appenders.get(i).getName(), Level.ALL.toString(), null);
		}

		// add our translator
		RewriteAppender rewriter = RewriteAppender.createAppender(
			"name",
			"true",
			refs,
			root.getContext().getConfiguration(),
			new TranslatingRewritePolicy(),
			null // no filter
		);
		rewriter.start();
		root.addAppender(rewriter);
	}
}
