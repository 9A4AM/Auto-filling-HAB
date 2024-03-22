package b4a.example3;


import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.B4AClass;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.debug.*;

public class doubletaptoclose extends B4AClass.ImplB4AClass implements BA.SubDelegator{
    private static java.util.HashMap<String, java.lang.reflect.Method> htSubs;
    private void innerInitialize(BA _ba) throws Exception {
        if (ba == null) {
            ba = new BA(_ba, this, htSubs, "b4a.example3.doubletaptoclose");
            if (htSubs == null) {
                ba.loadHtSubs(this.getClass());
                htSubs = ba.htSubs;
            }
            
        }
        if (BA.isShellModeRuntimeCheck(ba)) 
			   this.getClass().getMethod("_class_globals", b4a.example3.doubletaptoclose.class).invoke(this, new Object[] {null});
        else
            ba.raiseEvent2(null, true, "class_globals", false);
    }

 public anywheresoftware.b4a.keywords.Common __c = null;
public anywheresoftware.b4a.objects.Timer _ti = null;
public int _i = 0;
public String _tt = "";
public Object _beforecloseobj = null;
public String _beforecloseevent = "";
public int _cc = 0;
public b4a.example3.main _main = null;
public b4a.example3.starter _starter = null;
public String  _class_globals() throws Exception{
 //BA.debugLineNum = 2;BA.debugLine="Private Sub Class_Globals";
 //BA.debugLineNum = 3;BA.debugLine="Private Ti As Timer";
_ti = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 4;BA.debugLine="Private I As Int";
_i = 0;
 //BA.debugLineNum = 5;BA.debugLine="Private TT As String";
_tt = "";
 //BA.debugLineNum = 6;BA.debugLine="Private BeforeCloseObj As Object";
_beforecloseobj = new Object();
 //BA.debugLineNum = 7;BA.debugLine="Private BeforeCloseEvent As String";
_beforecloseevent = "";
 //BA.debugLineNum = 8;BA.debugLine="Private CC As Int";
_cc = 0;
 //BA.debugLineNum = 9;BA.debugLine="End Sub";
return "";
}
public String  _initialize(anywheresoftware.b4a.BA _ba,String _toasttext,Object _closeobject,String _closeevent,int _clickcount,int _clicktimeoutms) throws Exception{
innerInitialize(_ba);
 //BA.debugLineNum = 11;BA.debugLine="Public Sub Initialize (ToastText As String,CloseOb";
 //BA.debugLineNum = 12;BA.debugLine="I = 0";
_i = (int) (0);
 //BA.debugLineNum = 13;BA.debugLine="Ti.Initialize (\"Ti\",ClickTimeoutMs)";
_ti.Initialize(ba,"Ti",(long) (_clicktimeoutms));
 //BA.debugLineNum = 14;BA.debugLine="Ti.Enabled = False";
_ti.setEnabled(__c.False);
 //BA.debugLineNum = 15;BA.debugLine="TT = ToastText";
_tt = _toasttext;
 //BA.debugLineNum = 16;BA.debugLine="CC = ClickCount";
_cc = _clickcount;
 //BA.debugLineNum = 17;BA.debugLine="BeforeCloseObj = CloseObject";
_beforecloseobj = _closeobject;
 //BA.debugLineNum = 18;BA.debugLine="BeforeCloseEvent = CloseEvent";
_beforecloseevent = _closeevent;
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
public String  _taptoclose() throws Exception{
 //BA.debugLineNum = 25;BA.debugLine="Public Sub TapToClose";
 //BA.debugLineNum = 26;BA.debugLine="Ti.Enabled = False";
_ti.setEnabled(__c.False);
 //BA.debugLineNum = 27;BA.debugLine="Ti.Enabled = True";
_ti.setEnabled(__c.True);
 //BA.debugLineNum = 28;BA.debugLine="ToastMessageShow (TT,False)";
__c.ToastMessageShow(BA.ObjectToCharSequence(_tt),__c.False);
 //BA.debugLineNum = 29;BA.debugLine="I = I + 1";
_i = (int) (_i+1);
 //BA.debugLineNum = 30;BA.debugLine="If I >= CC Then";
if (_i>=_cc) { 
 //BA.debugLineNum = 31;BA.debugLine="Ti.Enabled = False";
_ti.setEnabled(__c.False);
 //BA.debugLineNum = 32;BA.debugLine="If (BeforeCloseObj <> Null) And (BeforeCloseEven";
if ((_beforecloseobj!= null) && ((_beforecloseevent).equals("") == false)) { 
__c.CallSubNew(ba,_beforecloseobj,_beforecloseevent);};
 //BA.debugLineNum = 33;BA.debugLine="ExitApplication";
__c.ExitApplication();
 };
 //BA.debugLineNum = 35;BA.debugLine="End Sub";
return "";
}
public String  _ti_tick() throws Exception{
 //BA.debugLineNum = 21;BA.debugLine="Private Sub Ti_Tick";
 //BA.debugLineNum = 22;BA.debugLine="I = 0";
_i = (int) (0);
 //BA.debugLineNum = 23;BA.debugLine="End Sub";
return "";
}
public Object callSub(String sub, Object sender, Object[] args) throws Exception {
BA.senderHolder.set(sender);
return BA.SubDelegator.SubNotFound;
}
}
