package cuchaz.m3l.tweaker;

import java.util.Map;

import net.minecraft.launchwrapper.Launch;
import cuchaz.m3l.Side;
import cuchaz.m3l.util.Arguments;


public abstract class CallbackTweaker extends Tweaker {
	
	protected CallbackTweaker(Class<?> callbackClass) {
		super(callbackClass.getName(), TweakerClassTransformerClient.class.getName(), Side.Client);
	}
	
	public static <T extends CallbackTweaker> void launch(Class<T> tweakerType) {
		launch(tweakerType, null);
	}
	
	public static <T extends CallbackTweaker> void launch(Class<T> tweakerType, Map<String,String> args) {
		
		// build the base arguments
		Arguments arguments = new Arguments();
		arguments.set("tweakClass", tweakerType.getName());
		arguments.set("userProperties", "{}");
		arguments.set("accessToken", "M3Luser");
		
		// add more arguments if needed
		if (args != null) {
			for (Map.Entry<String,String> arg : args.entrySet()) {
				arguments.set(arg.getKey(), arg.getValue());
			}
		}
		
		Launch.main(arguments.build());
	}
}
