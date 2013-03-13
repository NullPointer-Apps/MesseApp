package com.messedagliavr.messeapp;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey="dEhxTHF5WGc0SXl6U0hFaVlCb0QtdEE6MQ")
public class Acra extends Application {
	@Override
	public void onCreate() {
		
		// The following line triggers the initialization of ACRA
		ACRA.init(this);
		super.onCreate();
	}
}