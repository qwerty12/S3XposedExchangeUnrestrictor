package pk.qwerty12.s3xposedexchangeunrestrictor;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class S3XposedExchangeUnrestrictor implements IXposedHookLoadPackage {

	private static final String PACKAGE_EMAIL = "com.android.email";
	private static final String PACKAGE_EXCHANGE = "com.android.exchange";

	private void handleEmail(LoadPackageParam lpparam) {
		try {
			final Class<?> classSecurityPolicy = XposedHelpers.findClass(PACKAGE_EMAIL + ".SecurityPolicy", lpparam.classLoader);
			final Class<?> classPolicySet = XposedHelpers.findClass("com.android.emailcommon.service.PolicySet", lpparam.classLoader);

			XposedHelpers.findAndHookMethod(classSecurityPolicy, "getInactiveReasons", classPolicySet, 
				new XC_MethodHook()
				{
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable
					{
						param.setResult(0);
					}
				});

			XposedHelpers.findAndHookMethod(classSecurityPolicy, "isActive", classPolicySet, 
				new XC_MethodHook()
				{
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable
					{
						param.setResult(Boolean.TRUE);
					}
				});

			XposedHelpers.findAndHookMethod(classSecurityPolicy, "isActiveAdmin", 
				new XC_MethodHook()
				{
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable
					{
						param.setResult(Boolean.TRUE);
					}
				});

			XposedHelpers.findAndHookMethod(classSecurityPolicy, "isSupported", classPolicySet, 
				new XC_MethodHook()
				{
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable
					{
						param.setResult(Boolean.TRUE);
					}
				});
		} catch (Throwable t) { XposedBridge.log(t); }		
	}

	private void handleExchange(LoadPackageParam lpparam) {
		try {
			XposedHelpers.findAndHookMethod(PACKAGE_EXCHANGE + ".adapter.ProvisionParser", lpparam.classLoader, "hasSupportablePolicySet", 
				new XC_MethodHook()
				{
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable
					{
						XposedHelpers.setBooleanField(param.thisObject, "mIsSupportable", true);
					}
				});
		} catch (Throwable t) { XposedBridge.log(t); }
	}

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (lpparam.packageName.equals(PACKAGE_EMAIL))
			handleEmail(lpparam);

		if (lpparam.packageName.equals(PACKAGE_EXCHANGE))
			handleExchange(lpparam);
	}

}