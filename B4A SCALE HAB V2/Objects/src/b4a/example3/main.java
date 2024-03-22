package b4a.example3;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;
    public static boolean dontPause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.example3", "b4a.example3.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "b4a.example3", "b4a.example3.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example3.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create " + (isFirst ? "(first time)" : "") + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        if (!dontPause)
            BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (main) Pause event (activity is not paused). **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        if (!dontPause) {
            processBA.setActivityPaused(true);
            mostCurrent = null;
        }

        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            main mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.Serial.BluetoothAdmin _admin = null;
public static anywheresoftware.b4a.objects.Serial _serial1 = null;
public static b4a.example3.asyncstreamstext _ast = null;
public static String _mresult = "";
public static String _mresult_mode = "";
public static String _mresult_lift = "";
public static String _mresult_auto = "";
public static String _mresul_tare = "";
public static String _recv = "";
public static float _out = 0f;
public b4a.example3.gauge _gauge1 = null;
public b4a.example3.gauge _gauge2 = null;
public anywheresoftware.b4a.objects.B4XViewWrapper.XUI _xui = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label3 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label4 = null;
public anywheresoftware.b4a.objects.streams.File.TextWriterWrapper _podaci = null;
public anywheresoftware.b4a.phone.Phone.PhoneWakeState _ekran = null;
public b4a.example3.doubletaptoclose _d = null;
public static boolean _connected = false;
public anywheresoftware.b4a.objects.ImageViewWrapper _imageview1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label5 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label6 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label8 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label7 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label9 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label10 = null;
public static String _flag1 = "";
public anywheresoftware.b4a.objects.LabelWrapper _label11 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button3 = null;
public b4a.example3.starter _starter = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static class _nameandmac{
public boolean IsInitialized;
public String Name;
public String Mac;
public void Initialize() {
IsInitialized = true;
Name = "";
Mac = "";
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 72;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 73;BA.debugLine="Activity.LoadLayout(\"2\")";
mostCurrent._activity.LoadLayout("2",mostCurrent.activityBA);
 //BA.debugLineNum = 74;BA.debugLine="D.Initialize (\"Tap BACK again to exit\",Me,\"Before";
mostCurrent._d._initialize /*String*/ (processBA,"Tap BACK again to exit",main.getObject(),"BeforeClose",(int) (2),(int) (2000));
 //BA.debugLineNum = 75;BA.debugLine="Gauge1.SetRanges(Array(Gauge1.CreateRange(0, 8, x";
mostCurrent._gauge1._setranges /*String*/ (anywheresoftware.b4a.keywords.Common.ArrayToList(new Object[]{(Object)(mostCurrent._gauge1._createrange /*b4a.example3.gauge._gaugerange*/ ((float) (0),(float) (8),mostCurrent._xui.Color_Yellow)),(Object)(mostCurrent._gauge1._createrange /*b4a.example3.gauge._gaugerange*/ ((float) (8),(float) (10),mostCurrent._xui.Color_Green)),(Object)(mostCurrent._gauge1._createrange /*b4a.example3.gauge._gaugerange*/ ((float) (10),(float) (20),mostCurrent._xui.Color_Red))}));
 //BA.debugLineNum = 77;BA.debugLine="Gauge1.CurrentValue = 0";
mostCurrent._gauge1._setcurrentvalue /*float*/ ((float) (0));
 //BA.debugLineNum = 78;BA.debugLine="serial1.Disconnect";
_serial1.Disconnect();
 //BA.debugLineNum = 79;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 80;BA.debugLine="admin.Initialize(\"admin\")";
_admin.Initialize(processBA,"admin");
 //BA.debugLineNum = 81;BA.debugLine="serial1.Initialize(\"Serial1\")";
_serial1.Initialize("Serial1");
 };
 //BA.debugLineNum = 84;BA.debugLine="Activity.AddMenuItem(\"Connect\", \"mnuConnect\")";
mostCurrent._activity.AddMenuItem(BA.ObjectToCharSequence("Connect"),"mnuConnect");
 //BA.debugLineNum = 85;BA.debugLine="Activity.AddMenuItem(\"Disconnect\", \"mnuDisc\")";
mostCurrent._activity.AddMenuItem(BA.ObjectToCharSequence("Disconnect"),"mnuDisc");
 //BA.debugLineNum = 86;BA.debugLine="Activity.AddMenuItem(\"Disconnect&Exit\", \"mnuDisco";
mostCurrent._activity.AddMenuItem(BA.ObjectToCharSequence("Disconnect&Exit"),"mnuDisconnect");
 //BA.debugLineNum = 87;BA.debugLine="Label9.Visible = False";
mostCurrent._label9.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 88;BA.debugLine="Label10.Visible = True";
mostCurrent._label10.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 89;BA.debugLine="Label11.Visible = True";
mostCurrent._label11.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 90;BA.debugLine="Flag1= \"prg_helium\"";
mostCurrent._flag1 = "prg_helium";
 //BA.debugLineNum = 91;BA.debugLine="mResult_mode = \"prg_hel\"";
_mresult_mode = "prg_hel";
 //BA.debugLineNum = 92;BA.debugLine="mResult_lift = \"8\"";
_mresult_lift = "8";
 //BA.debugLineNum = 93;BA.debugLine="mResult_auto =\"man\"";
_mresult_auto = "man";
 //BA.debugLineNum = 94;BA.debugLine="mResul_Tare = \"TN\"";
_mresul_tare = "TN";
 //BA.debugLineNum = 95;BA.debugLine="Button1.Color = Colors.Yellow";
mostCurrent._button1.setColor(anywheresoftware.b4a.keywords.Common.Colors.Yellow);
 //BA.debugLineNum = 96;BA.debugLine="Button1.Text = \"AUTO FILLING INACTIV\"";
mostCurrent._button1.setText(BA.ObjectToCharSequence("AUTO FILLING INACTIV"));
 //BA.debugLineNum = 98;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
 //BA.debugLineNum = 189;BA.debugLine="Sub Activity_KeyPress (KeyCode As Int) As Boolean";
 //BA.debugLineNum = 190;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 //BA.debugLineNum = 191;BA.debugLine="D.TapToClose";
mostCurrent._d._taptoclose /*String*/ ();
 //BA.debugLineNum = 192;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 194;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 174;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 175;BA.debugLine="Ekran.ReleaseKeepAlive";
mostCurrent._ekran.ReleaseKeepAlive();
 //BA.debugLineNum = 176;BA.debugLine="Ekran.ReleasePartialLock";
mostCurrent._ekran.ReleasePartialLock();
 //BA.debugLineNum = 177;BA.debugLine="If UserClosed Then";
if (_userclosed) { 
 //BA.debugLineNum = 178;BA.debugLine="podaci.Close";
mostCurrent._podaci.Close();
 //BA.debugLineNum = 179;BA.debugLine="ast.Close";
_ast._close /*String*/ ();
 //BA.debugLineNum = 180;BA.debugLine="serial1.Disconnect";
_serial1.Disconnect();
 //BA.debugLineNum = 181;BA.debugLine="mResult=\"\"";
_mresult = "";
 //BA.debugLineNum = 183;BA.debugLine="admin.Disable";
_admin.Disable();
 };
 //BA.debugLineNum = 186;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 152;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 153;BA.debugLine="Ekran.KeepAlive(True)";
mostCurrent._ekran.KeepAlive(processBA,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 154;BA.debugLine="Ekran.PartialLock";
mostCurrent._ekran.PartialLock(processBA);
 //BA.debugLineNum = 156;BA.debugLine="If admin.IsEnabled = False Then";
if (_admin.IsEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 157;BA.debugLine="If admin.Enable = False Then";
if (_admin.Enable()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 158;BA.debugLine="ToastMessageShow(\"Error enabling Bluetooth adap";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Error enabling Bluetooth adapter."),anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 160;BA.debugLine="ToastMessageShow(\"Enabling Bluetooth adapter...";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Enabling Bluetooth adapter..."),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 163;BA.debugLine="Wait(4)";
_wait((int) (4));
 //BA.debugLineNum = 164;BA.debugLine="Msgbox(\"Please power on scale and confirm OK!!\"";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("Please power on scale and confirm OK!!"),BA.ObjectToCharSequence("Wait please!"),mostCurrent.activityBA);
 };
 }else {
 //BA.debugLineNum = 169;BA.debugLine="Admin_StateChanged(admin.STATE_ON, 0)";
_admin_statechanged(_admin.STATE_ON,(int) (0));
 };
 //BA.debugLineNum = 172;BA.debugLine="End Sub";
return "";
}
public static String  _admin_statechanged(int _newstate,int _oldstate) throws Exception{
 //BA.debugLineNum = 377;BA.debugLine="Sub Admin_StateChanged (NewState As Int, OldState";
 //BA.debugLineNum = 381;BA.debugLine="End Sub";
return "";
}
public static String  _ast_error() throws Exception{
 //BA.debugLineNum = 296;BA.debugLine="Sub ast_Error";
 //BA.debugLineNum = 297;BA.debugLine="ToastMessageShow(\"Network Error: \" & LastExceptio";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Network Error: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage()),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 298;BA.debugLine="End Sub";
return "";
}
public static String  _ast_newtext(String _text) throws Exception{
float _rec_ive = 0f;
float _mr_lift = 0f;
 //BA.debugLineNum = 304;BA.debugLine="Sub ast_NewText( Text As String)";
 //BA.debugLineNum = 308;BA.debugLine="Dim Rec_ive As Float";
_rec_ive = 0f;
 //BA.debugLineNum = 309;BA.debugLine="Dim mR_lift As Float";
_mr_lift = 0f;
 //BA.debugLineNum = 310;BA.debugLine="Recv = Text";
_recv = _text;
 //BA.debugLineNum = 311;BA.debugLine="Label11.Text = Recv";
mostCurrent._label11.setText(BA.ObjectToCharSequence(_recv));
 //BA.debugLineNum = 312;BA.debugLine="If IsNumber(Recv) Then";
if (anywheresoftware.b4a.keywords.Common.IsNumber(_recv)) { 
 //BA.debugLineNum = 313;BA.debugLine="Out = Recv";
_out = (float)(Double.parseDouble(_recv));
 //BA.debugLineNum = 314;BA.debugLine="Gauge1.CurrentValue = Out";
mostCurrent._gauge1._setcurrentvalue /*float*/ (_out);
 //BA.debugLineNum = 315;BA.debugLine="Rec_ive = Recv";
_rec_ive = (float)(Double.parseDouble(_recv));
 //BA.debugLineNum = 316;BA.debugLine="mR_lift = mResult_lift";
_mr_lift = (float)(Double.parseDouble(_mresult_lift));
 //BA.debugLineNum = 317;BA.debugLine="If mResult_mode = \"hel\" And mResult_auto = \"aut\"";
if ((_mresult_mode).equals("hel") && (_mresult_auto).equals("aut")) { 
 //BA.debugLineNum = 318;BA.debugLine="If Rec_ive >= mR_lift And mResult_auto = \"aut\" T";
if (_rec_ive>=_mr_lift && (_mresult_auto).equals("aut")) { 
 //BA.debugLineNum = 319;BA.debugLine="mResult_auto = \"man\"";
_mresult_auto = "man";
 //BA.debugLineNum = 320;BA.debugLine="Button1.Color = Colors.Red";
mostCurrent._button1.setColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 321;BA.debugLine="Button1.Text = \"AUTO FILLING COMPLETE\"";
mostCurrent._button1.setText(BA.ObjectToCharSequence("AUTO FILLING COMPLETE"));
 //BA.debugLineNum = 322;BA.debugLine="Label2.Color = Colors.Red";
mostCurrent._label2.setColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 323;BA.debugLine="Label5.Color = Colors.Red";
mostCurrent._label5.setColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 324;BA.debugLine="Label2.Text = \"Valve FAST fill OFF\"";
mostCurrent._label2.setText(BA.ObjectToCharSequence("Valve FAST fill OFF"));
 //BA.debugLineNum = 325;BA.debugLine="Label5.Text = \"Valve FAST fill OFF\"";
mostCurrent._label5.setText(BA.ObjectToCharSequence("Valve FAST fill OFF"));
 };
 //BA.debugLineNum = 327;BA.debugLine="If Rec_ive < mR_lift And Rec_ive < (mR_lift - 1)";
if (_rec_ive<_mr_lift && _rec_ive<(_mr_lift-1) && (_mresult_auto).equals("aut")) { 
 //BA.debugLineNum = 328;BA.debugLine="Label2.Color = Colors.Green";
mostCurrent._label2.setColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 329;BA.debugLine="Label5.Color = Colors.Green";
mostCurrent._label5.setColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 330;BA.debugLine="Label2.Text = \"Valve FAST fill ON\"";
mostCurrent._label2.setText(BA.ObjectToCharSequence("Valve FAST fill ON"));
 //BA.debugLineNum = 331;BA.debugLine="Label5.Text = \"Valve FAST fill ON\"";
mostCurrent._label5.setText(BA.ObjectToCharSequence("Valve FAST fill ON"));
 };
 //BA.debugLineNum = 334;BA.debugLine="If Rec_ive < mR_lift And Rec_ive > (mR_lift - 1)";
if (_rec_ive<_mr_lift && _rec_ive>(_mr_lift-1) && (_mresult_auto).equals("aut")) { 
 //BA.debugLineNum = 335;BA.debugLine="Label2.Color = Colors.Red";
mostCurrent._label2.setColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 336;BA.debugLine="Label5.Color = Colors.Green";
mostCurrent._label5.setColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 337;BA.debugLine="Label2.Text = \"Valve FAST fill OFF\"";
mostCurrent._label2.setText(BA.ObjectToCharSequence("Valve FAST fill OFF"));
 //BA.debugLineNum = 338;BA.debugLine="Label5.Text = \"Valve FAST fill ON\"";
mostCurrent._label5.setText(BA.ObjectToCharSequence("Valve FAST fill ON"));
 };
 }else {
 //BA.debugLineNum = 342;BA.debugLine="If Rec_ive >= mR_lift And mResult_auto = \"aut\"";
if (_rec_ive>=_mr_lift && (_mresult_auto).equals("aut")) { 
 //BA.debugLineNum = 343;BA.debugLine="mResult_auto = \"man\"";
_mresult_auto = "man";
 //BA.debugLineNum = 344;BA.debugLine="Button1.Color = Colors.Red";
mostCurrent._button1.setColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 345;BA.debugLine="Button1.Text = \"AUTO FILLING COMPLETE\"";
mostCurrent._button1.setText(BA.ObjectToCharSequence("AUTO FILLING COMPLETE"));
 //BA.debugLineNum = 346;BA.debugLine="Label2.Color = Colors.Red";
mostCurrent._label2.setColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 347;BA.debugLine="Label5.Color = Colors.Red";
mostCurrent._label5.setColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 348;BA.debugLine="Label6.Color = Colors.Green";
mostCurrent._label6.setColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 349;BA.debugLine="Label2.Text = \"Valve FAST fill OFF\"";
mostCurrent._label2.setText(BA.ObjectToCharSequence("Valve FAST fill OFF"));
 //BA.debugLineNum = 350;BA.debugLine="Label5.Text = \"Valve FAST fill OFF\"";
mostCurrent._label5.setText(BA.ObjectToCharSequence("Valve FAST fill OFF"));
 //BA.debugLineNum = 351;BA.debugLine="Label6.Text = \"Valve EXIT ON\"";
mostCurrent._label6.setText(BA.ObjectToCharSequence("Valve EXIT ON"));
 };
 //BA.debugLineNum = 353;BA.debugLine="If Rec_ive < mR_lift And Rec_ive < (mR_lift - 1";
if (_rec_ive<_mr_lift && _rec_ive<(_mr_lift-1) && (_mresult_auto).equals("aut")) { 
 //BA.debugLineNum = 354;BA.debugLine="Label2.Color = Colors.Green";
mostCurrent._label2.setColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 355;BA.debugLine="Label5.Color = Colors.Green";
mostCurrent._label5.setColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 356;BA.debugLine="Label6.Color = Colors.Red";
mostCurrent._label6.setColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 357;BA.debugLine="Label2.Text = \"Valve FAST fill ON\"";
mostCurrent._label2.setText(BA.ObjectToCharSequence("Valve FAST fill ON"));
 //BA.debugLineNum = 358;BA.debugLine="Label5.Text = \"Valve FAST fill ON\"";
mostCurrent._label5.setText(BA.ObjectToCharSequence("Valve FAST fill ON"));
 //BA.debugLineNum = 359;BA.debugLine="Label6.Text = \"Valve EXIT OFF\"";
mostCurrent._label6.setText(BA.ObjectToCharSequence("Valve EXIT OFF"));
 };
 //BA.debugLineNum = 362;BA.debugLine="If Rec_ive < mR_lift And Rec_ive > (mR_lift - 1";
if (_rec_ive<_mr_lift && _rec_ive>(_mr_lift-1) && (_mresult_auto).equals("aut")) { 
 //BA.debugLineNum = 363;BA.debugLine="Label2.Color = Colors.Red";
mostCurrent._label2.setColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 364;BA.debugLine="Label5.Color = Colors.Green";
mostCurrent._label5.setColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 365;BA.debugLine="Label6.Color = Colors.Red";
mostCurrent._label6.setColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 366;BA.debugLine="Label2.Text = \"Valve FAST fill OFF\"";
mostCurrent._label2.setText(BA.ObjectToCharSequence("Valve FAST fill OFF"));
 //BA.debugLineNum = 367;BA.debugLine="Label5.Text = \"Valve FAST fill ON\"";
mostCurrent._label5.setText(BA.ObjectToCharSequence("Valve FAST fill ON"));
 //BA.debugLineNum = 368;BA.debugLine="Label6.Text = \"Valve EXIT OFF\"";
mostCurrent._label6.setText(BA.ObjectToCharSequence("Valve EXIT OFF"));
 };
 };
 };
 //BA.debugLineNum = 375;BA.debugLine="End Sub";
return "";
}
public static String  _ast_terminated() throws Exception{
 //BA.debugLineNum = 300;BA.debugLine="Sub ast_Terminated";
 //BA.debugLineNum = 301;BA.debugLine="ToastMessageShow(\"Broken Connection !!!\",True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Broken Connection !!!"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 302;BA.debugLine="End Sub";
return "";
}
public static String  _astream_error() throws Exception{
 //BA.debugLineNum = 393;BA.debugLine="Sub AStream_Error";
 //BA.debugLineNum = 394;BA.debugLine="ToastMessageShow(\"Connection is broken.\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Connection is broken."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 395;BA.debugLine="Label4.Visible = True";
mostCurrent._label4.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 396;BA.debugLine="Label3.Visible = False";
mostCurrent._label3.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 397;BA.debugLine="End Sub";
return "";
}
public static String  _astream_newdata(byte[] _buffer) throws Exception{
String _msg = "";
 //BA.debugLineNum = 383;BA.debugLine="Sub AStream_NewData (Buffer() As Byte)";
 //BA.debugLineNum = 387;BA.debugLine="Dim msg As String";
_msg = "";
 //BA.debugLineNum = 388;BA.debugLine="msg = BytesToString(Buffer, 0, Buffer.Length, \"UT";
_msg = anywheresoftware.b4a.keywords.Common.BytesToString(_buffer,(int) (0),_buffer.length,"UTF8");
 //BA.debugLineNum = 389;BA.debugLine="ToastMessageShow(msg, False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence(_msg),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 390;BA.debugLine="End Sub";
return "";
}
public static String  _astream_terminated() throws Exception{
 //BA.debugLineNum = 399;BA.debugLine="Sub AStream_Terminated";
 //BA.debugLineNum = 400;BA.debugLine="AStream_Error";
_astream_error();
 //BA.debugLineNum = 401;BA.debugLine="End Sub";
return "";
}
public static String  _beforeclose() throws Exception{
 //BA.debugLineNum = 196;BA.debugLine="Sub BeforeClose";
 //BA.debugLineNum = 198;BA.debugLine="If connected = True Then";
if (_connected==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 199;BA.debugLine="podaci.Close";
mostCurrent._podaci.Close();
 //BA.debugLineNum = 200;BA.debugLine="serial1.Disconnect";
_serial1.Disconnect();
 };
 //BA.debugLineNum = 204;BA.debugLine="Label4.Visible = True";
mostCurrent._label4.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 205;BA.debugLine="Label3.Visible = False";
mostCurrent._label3.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 206;BA.debugLine="End Sub";
return "";
}
public static String  _button1_click() throws Exception{
 //BA.debugLineNum = 100;BA.debugLine="Sub Button1_Click";
 //BA.debugLineNum = 102;BA.debugLine="mResult_auto = \"aut\"";
_mresult_auto = "aut";
 //BA.debugLineNum = 103;BA.debugLine="Button1.Color = Colors.Green";
mostCurrent._button1.setColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 104;BA.debugLine="Button1.Text = \"AUTO FILLING ACTIV\"";
mostCurrent._button1.setText(BA.ObjectToCharSequence("AUTO FILLING ACTIV"));
 //BA.debugLineNum = 107;BA.debugLine="SEND_Data";
_send_data();
 //BA.debugLineNum = 114;BA.debugLine="End Sub";
return "";
}
public static String  _button2_click() throws Exception{
 //BA.debugLineNum = 117;BA.debugLine="Sub Button2_Click";
 //BA.debugLineNum = 121;BA.debugLine="If Flag1 = \"prg_helium\" Then";
if ((mostCurrent._flag1).equals("prg_helium")) { 
 //BA.debugLineNum = 122;BA.debugLine="Label9.Visible = True";
mostCurrent._label9.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 123;BA.debugLine="Label10.Visible = False";
mostCurrent._label10.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 124;BA.debugLine="Label6.Color = Colors.Green";
mostCurrent._label6.setColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 125;BA.debugLine="mResult_mode = \"prg_hyd\"";
_mresult_mode = "prg_hyd";
 //BA.debugLineNum = 126;BA.debugLine="Flag1 = \"prg_hydrogen\"";
mostCurrent._flag1 = "prg_hydrogen";
 }else {
 //BA.debugLineNum = 128;BA.debugLine="Label9.Visible = False";
mostCurrent._label9.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 129;BA.debugLine="Label10.Visible = True";
mostCurrent._label10.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 130;BA.debugLine="Label6.Color = Colors.Red";
mostCurrent._label6.setColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 131;BA.debugLine="mResult_mode = \"prg_hel\"";
_mresult_mode = "prg_hel";
 //BA.debugLineNum = 132;BA.debugLine="Flag1 = \"prg_helium\"";
mostCurrent._flag1 = "prg_helium";
 };
 //BA.debugLineNum = 136;BA.debugLine="SEND_Data";
_send_data();
 //BA.debugLineNum = 138;BA.debugLine="End Sub";
return "";
}
public static String  _button3_click() throws Exception{
 //BA.debugLineNum = 140;BA.debugLine="Sub Button3_Click";
 //BA.debugLineNum = 144;BA.debugLine="mResul_Tare = \"TY\"";
_mresul_tare = "TY";
 //BA.debugLineNum = 147;BA.debugLine="SEND_Data";
_send_data();
 //BA.debugLineNum = 149;BA.debugLine="End Sub";
return "";
}
public static int  _cint(Object _o) throws Exception{
 //BA.debugLineNum = 436;BA.debugLine="Sub CInt(o As Object) As Int";
 //BA.debugLineNum = 437;BA.debugLine="Return Floor(o)";
if (true) return (int) (anywheresoftware.b4a.keywords.Common.Floor((double)(BA.ObjectToNumber(_o))));
 //BA.debugLineNum = 438;BA.debugLine="End Sub";
return 0;
}
public static long  _clng(Object _o) throws Exception{
 //BA.debugLineNum = 440;BA.debugLine="Sub CLng(o As Object) As Long";
 //BA.debugLineNum = 441;BA.debugLine="Return Floor(o)";
if (true) return (long) (anywheresoftware.b4a.keywords.Common.Floor((double)(BA.ObjectToNumber(_o))));
 //BA.debugLineNum = 442;BA.debugLine="End Sub";
return 0L;
}
public static String  _cstr(Object _o) throws Exception{
 //BA.debugLineNum = 432;BA.debugLine="Sub CStr(o As Object) As String";
 //BA.debugLineNum = 433;BA.debugLine="Return \"\" & o";
if (true) return ""+BA.ObjectToString(_o);
 //BA.debugLineNum = 434;BA.debugLine="End Sub";
return "";
}
public static String  _edittext1_enterpressed() throws Exception{
 //BA.debugLineNum = 419;BA.debugLine="Private Sub EditText1_EnterPressed";
 //BA.debugLineNum = 421;BA.debugLine="ToastMessageShow(\"Value must be betwen 0 and 20!\"";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Value must be betwen 0 and 20!"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 422;BA.debugLine="Button1.Color = Colors.Yellow";
mostCurrent._button1.setColor(anywheresoftware.b4a.keywords.Common.Colors.Yellow);
 //BA.debugLineNum = 423;BA.debugLine="Button1.Text = \"AUTO FILLING INACTIV\"";
mostCurrent._button1.setText(BA.ObjectToCharSequence("AUTO FILLING INACTIV"));
 //BA.debugLineNum = 424;BA.debugLine="If EditText1.Text = \"\"  Then";
if ((mostCurrent._edittext1.getText()).equals("")) { 
 //BA.debugLineNum = 425;BA.debugLine="EditText1.Text = \"8\"";
mostCurrent._edittext1.setText(BA.ObjectToCharSequence("8"));
 };
 //BA.debugLineNum = 427;BA.debugLine="mResult_lift = EditText1.Text";
_mresult_lift = mostCurrent._edittext1.getText();
 //BA.debugLineNum = 428;BA.debugLine="SEND_Data";
_send_data();
 //BA.debugLineNum = 430;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 38;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 40;BA.debugLine="Private Gauge1 As Gauge";
mostCurrent._gauge1 = new b4a.example3.gauge();
 //BA.debugLineNum = 41;BA.debugLine="Private Gauge2 As Gauge";
mostCurrent._gauge2 = new b4a.example3.gauge();
 //BA.debugLineNum = 42;BA.debugLine="Private xui As XUI";
mostCurrent._xui = new anywheresoftware.b4a.objects.B4XViewWrapper.XUI();
 //BA.debugLineNum = 43;BA.debugLine="Private Button1 As Button";
mostCurrent._button1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 44;BA.debugLine="Private Label3 As Label";
mostCurrent._label3 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 45;BA.debugLine="Private Label4 As Label";
mostCurrent._label4 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 46;BA.debugLine="Dim podaci As TextWriter";
mostCurrent._podaci = new anywheresoftware.b4a.objects.streams.File.TextWriterWrapper();
 //BA.debugLineNum = 47;BA.debugLine="Dim Ekran As PhoneWakeState";
mostCurrent._ekran = new anywheresoftware.b4a.phone.Phone.PhoneWakeState();
 //BA.debugLineNum = 48;BA.debugLine="Dim D As DoubleTaptoClose";
mostCurrent._d = new b4a.example3.doubletaptoclose();
 //BA.debugLineNum = 51;BA.debugLine="Dim connected As Boolean";
_connected = false;
 //BA.debugLineNum = 55;BA.debugLine="Private ImageView1 As ImageView";
mostCurrent._imageview1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 56;BA.debugLine="Private Label1 As Label";
mostCurrent._label1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 57;BA.debugLine="Private Label2 As Label";
mostCurrent._label2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 58;BA.debugLine="Private Label5 As Label";
mostCurrent._label5 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 59;BA.debugLine="Private Label6 As Label";
mostCurrent._label6 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 60;BA.debugLine="Private EditText1 As EditText";
mostCurrent._edittext1 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 61;BA.debugLine="Private Label8 As Label";
mostCurrent._label8 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 62;BA.debugLine="Private Label7 As Label";
mostCurrent._label7 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 63;BA.debugLine="Private Label9 As Label";
mostCurrent._label9 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 64;BA.debugLine="Private Button2 As Button";
mostCurrent._button2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 65;BA.debugLine="Private Label10 As Label";
mostCurrent._label10 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 66;BA.debugLine="Dim Flag1 As String";
mostCurrent._flag1 = "";
 //BA.debugLineNum = 68;BA.debugLine="Private Label11 As Label";
mostCurrent._label11 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 69;BA.debugLine="Private Button3 As Button";
mostCurrent._button3 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 70;BA.debugLine="End Sub";
return "";
}
public static String  _mnuconnect_click() throws Exception{
anywheresoftware.b4a.objects.collections.Map _paireddevices = null;
anywheresoftware.b4a.objects.collections.List _l = null;
int _i = 0;
int _res = 0;
 //BA.debugLineNum = 234;BA.debugLine="Sub mnuConnect_Click";
 //BA.debugLineNum = 235;BA.debugLine="If serial1.IsEnabled = False Then";
if (_serial1.IsEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 236;BA.debugLine="Msgbox(\"Please enable Bluetooth.\", \"\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("Please enable Bluetooth."),BA.ObjectToCharSequence(""),mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 238;BA.debugLine="serial1.Disconnect";
_serial1.Disconnect();
 //BA.debugLineNum = 239;BA.debugLine="Dim PairedDevices As Map";
_paireddevices = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 240;BA.debugLine="PairedDevices = serial1.GetPairedDevices";
_paireddevices = _serial1.GetPairedDevices();
 //BA.debugLineNum = 241;BA.debugLine="Dim l As List";
_l = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 242;BA.debugLine="l.Initialize";
_l.Initialize();
 //BA.debugLineNum = 243;BA.debugLine="For i = 0 To PairedDevices.Size - 1";
{
final int step9 = 1;
final int limit9 = (int) (_paireddevices.getSize()-1);
_i = (int) (0) ;
for (;_i <= limit9 ;_i = _i + step9 ) {
 //BA.debugLineNum = 244;BA.debugLine="l.Add(PairedDevices.GetKeyAt(i)) 'add the frien";
_l.Add(_paireddevices.GetKeyAt(_i));
 }
};
 //BA.debugLineNum = 246;BA.debugLine="Dim res As Int";
_res = 0;
 //BA.debugLineNum = 247;BA.debugLine="res = InputList(l, \"Choose device\", -1) 'show li";
_res = anywheresoftware.b4a.keywords.Common.InputList(_l,BA.ObjectToCharSequence("Choose device"),(int) (-1),mostCurrent.activityBA);
 //BA.debugLineNum = 248;BA.debugLine="If res <> DialogResponse.CANCEL Then";
if (_res!=anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL) { 
 //BA.debugLineNum = 249;BA.debugLine="serial1.Connect(PairedDevices.Get(l.Get(res)))";
_serial1.Connect(processBA,BA.ObjectToString(_paireddevices.Get(_l.Get(_res))));
 };
 };
 //BA.debugLineNum = 253;BA.debugLine="End Sub";
return "";
}
public static String  _mnudisc_click() throws Exception{
 //BA.debugLineNum = 266;BA.debugLine="Sub mnuDisc_Click";
 //BA.debugLineNum = 267;BA.debugLine="If connected = True Then";
if (_connected==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 268;BA.debugLine="podaci.Close";
mostCurrent._podaci.Close();
 //BA.debugLineNum = 269;BA.debugLine="serial1.Disconnect";
_serial1.Disconnect();
 };
 //BA.debugLineNum = 273;BA.debugLine="Label4.Visible = True";
mostCurrent._label4.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 274;BA.debugLine="Label3.Visible = False";
mostCurrent._label3.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 276;BA.debugLine="End Sub";
return "";
}
public static String  _mnudisconnect_click() throws Exception{
 //BA.debugLineNum = 254;BA.debugLine="Sub mnuDisconnect_Click";
 //BA.debugLineNum = 255;BA.debugLine="If connected = True Then";
if (_connected==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 256;BA.debugLine="podaci.Close";
mostCurrent._podaci.Close();
 //BA.debugLineNum = 257;BA.debugLine="serial1.Disconnect";
_serial1.Disconnect();
 };
 //BA.debugLineNum = 261;BA.debugLine="Label4.Visible = True";
mostCurrent._label4.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 262;BA.debugLine="Label3.Visible = False";
mostCurrent._label3.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 263;BA.debugLine="ExitApplication";
anywheresoftware.b4a.keywords.Common.ExitApplication();
 //BA.debugLineNum = 264;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
starter._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 16;BA.debugLine="Dim admin As BluetoothAdmin";
_admin = new anywheresoftware.b4a.objects.Serial.BluetoothAdmin();
 //BA.debugLineNum = 17;BA.debugLine="Dim serial1 As Serial";
_serial1 = new anywheresoftware.b4a.objects.Serial();
 //BA.debugLineNum = 19;BA.debugLine="Dim ast As AsyncStreamsText";
_ast = new b4a.example3.asyncstreamstext();
 //BA.debugLineNum = 20;BA.debugLine="Type NameAndMac (Name As String, Mac As String)";
;
 //BA.debugLineNum = 24;BA.debugLine="Dim mResult As String";
_mresult = "";
 //BA.debugLineNum = 25;BA.debugLine="Dim mResult_mode As String";
_mresult_mode = "";
 //BA.debugLineNum = 26;BA.debugLine="Dim mResult_lift As String";
_mresult_lift = "";
 //BA.debugLineNum = 27;BA.debugLine="Dim mResult_auto As String";
_mresult_auto = "";
 //BA.debugLineNum = 28;BA.debugLine="Dim mResul_Tare As String";
_mresul_tare = "";
 //BA.debugLineNum = 29;BA.debugLine="Dim Recv As String";
_recv = "";
 //BA.debugLineNum = 30;BA.debugLine="Dim Out As Float";
_out = 0f;
 //BA.debugLineNum = 31;BA.debugLine="mResult = \"\"";
_mresult = "";
 //BA.debugLineNum = 32;BA.debugLine="mResult_mode = \"\"";
_mresult_mode = "";
 //BA.debugLineNum = 33;BA.debugLine="mResult_lift = \"\"";
_mresult_lift = "";
 //BA.debugLineNum = 34;BA.debugLine="mResult_auto = \"\"";
_mresult_auto = "";
 //BA.debugLineNum = 35;BA.debugLine="mResul_Tare = \"\"";
_mresul_tare = "";
 //BA.debugLineNum = 36;BA.debugLine="End Sub";
return "";
}
public static String  _scan_click() throws Exception{
 //BA.debugLineNum = 277;BA.debugLine="Sub SCAN_Click";
 //BA.debugLineNum = 278;BA.debugLine="If connected Then";
if (_connected) { 
 //BA.debugLineNum = 279;BA.debugLine="ToastMessageShow(\"Filling started\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Filling started"),anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 281;BA.debugLine="End Sub";
return "";
}
public static String  _send_data() throws Exception{
 //BA.debugLineNum = 282;BA.debugLine="Sub SEND_Data";
 //BA.debugLineNum = 283;BA.debugLine="If connected Then";
if (_connected) { 
 //BA.debugLineNum = 285;BA.debugLine="ToastMessageShow(\"Data send.\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Data send."),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 286;BA.debugLine="mResult = (mResult_lift & \";\" & mResult_mode & \"";
_mresult = (_mresult_lift+";"+_mresult_mode+";"+_mresult_auto+";"+_mresul_tare+"*"+anywheresoftware.b4a.keywords.Common.CRLF);
 //BA.debugLineNum = 287;BA.debugLine="podaci.Write(mResult)";
mostCurrent._podaci.Write(_mresult);
 //BA.debugLineNum = 288;BA.debugLine="podaci.Flush";
mostCurrent._podaci.Flush();
 //BA.debugLineNum = 291;BA.debugLine="mResult = \"\"";
_mresult = "";
 //BA.debugLineNum = 292;BA.debugLine="mResul_Tare = \"TN\"";
_mresul_tare = "TN";
 };
 //BA.debugLineNum = 294;BA.debugLine="End Sub";
return "";
}
public static String  _serial1_connected(boolean _success) throws Exception{
 //BA.debugLineNum = 208;BA.debugLine="Sub Serial1_Connected (Success As Boolean)";
 //BA.debugLineNum = 210;BA.debugLine="If Success Then";
if (_success) { 
 //BA.debugLineNum = 211;BA.debugLine="ToastMessageShow(\"Connected successfully\", False";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Connected successfully"),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 214;BA.debugLine="connected = True";
_connected = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 216;BA.debugLine="Label3.Visible = True";
mostCurrent._label3.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 217;BA.debugLine="Label4.Visible = False";
mostCurrent._label4.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 218;BA.debugLine="If ast.IsInitialized Then ast.Close";
if (_ast.IsInitialized /*boolean*/ ()) { 
_ast._close /*String*/ ();};
 //BA.debugLineNum = 219;BA.debugLine="ast.Initialize(Me, \"ast\", serial1.InputStream, s";
_ast._initialize /*String*/ (processBA,main.getObject(),"ast",(anywheresoftware.b4a.objects.streams.File.InputStreamWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.streams.File.InputStreamWrapper(), (java.io.InputStream)(_serial1.getInputStream())),(anywheresoftware.b4a.objects.streams.File.OutputStreamWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.streams.File.OutputStreamWrapper(), (java.io.OutputStream)(_serial1.getOutputStream())));
 //BA.debugLineNum = 220;BA.debugLine="podaci.Initialize(serial1.OutputStream)";
mostCurrent._podaci.Initialize(_serial1.getOutputStream());
 //BA.debugLineNum = 221;BA.debugLine="mResult = (mResult_lift & \";\" & mResult_mode & \"";
_mresult = (_mresult_lift+";"+_mresult_mode+";"+_mresult_auto+";"+_mresul_tare+"*"+anywheresoftware.b4a.keywords.Common.CRLF);
 //BA.debugLineNum = 222;BA.debugLine="podaci.Write(mResult)";
mostCurrent._podaci.Write(_mresult);
 //BA.debugLineNum = 223;BA.debugLine="podaci.Flush";
mostCurrent._podaci.Flush();
 //BA.debugLineNum = 224;BA.debugLine="mResult=\"\"";
_mresult = "";
 }else {
 //BA.debugLineNum = 226;BA.debugLine="connected = False";
_connected = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 227;BA.debugLine="Label4.Visible = True";
mostCurrent._label4.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 228;BA.debugLine="Label3.Visible = False";
mostCurrent._label3.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 229;BA.debugLine="Msgbox(LastException.Message, \"Error connecting.";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage()),BA.ObjectToCharSequence("Error connecting."),mostCurrent.activityBA);
 };
 //BA.debugLineNum = 233;BA.debugLine="End Sub";
return "";
}
public static String  _wait(int _sekunden) throws Exception{
long _ti = 0L;
 //BA.debugLineNum = 410;BA.debugLine="Sub Wait(Sekunden As Int)";
 //BA.debugLineNum = 411;BA.debugLine="Dim Ti As Long";
_ti = 0L;
 //BA.debugLineNum = 412;BA.debugLine="Ti = DateTime.Now + (Sekunden * 1000)";
_ti = (long) (anywheresoftware.b4a.keywords.Common.DateTime.getNow()+(_sekunden*1000));
 //BA.debugLineNum = 413;BA.debugLine="Do While DateTime.Now < Ti";
while (anywheresoftware.b4a.keywords.Common.DateTime.getNow()<_ti) {
 //BA.debugLineNum = 414;BA.debugLine="DoEvents";
anywheresoftware.b4a.keywords.Common.DoEvents();
 }
;
 //BA.debugLineNum = 416;BA.debugLine="End Sub";
return "";
}
}
