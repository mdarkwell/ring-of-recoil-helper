package com.darkwell.rorh;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class RingOfRecoilPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(RingOfRecoilPlugin.class);
		RuneLite.main(args);
	}
}