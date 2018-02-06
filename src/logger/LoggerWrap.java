package logger;

import engine.Engine;
import enstabretagne.base.logger.Logger;

public class LoggerWrap {

	public static void setDateProvider(Engine engine) {
		Logger.setDateProvider(engine);
	}
	
	public static void Log(IRecordableWrapper recordable, String message) {
		recordable.setMsg(message);
		Logger.Data(recordable);
	}
	
	public static void Log(IRecordableWrapper recordable) {
		recordable.setMsg("No message");
		Logger.Data(recordable);
	}
	
	public static void Terminate() {
		Logger.Terminate();
	}
	
}