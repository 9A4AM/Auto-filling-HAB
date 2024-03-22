B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Class
Version=12.2
@EndOfDesignText@
'Class module
Private Sub Class_Globals
	Private Ti As Timer
	Private I As Int
	Private TT As String
	Private BeforeCloseObj As Object
	Private BeforeCloseEvent As String
	Private CC As Int
End Sub

Public Sub Initialize (ToastText As String,CloseObject As Object , CloseEvent As String,ClickCount As Int,ClickTimeoutMs As Int)
	I = 0
	Ti.Initialize ("Ti",ClickTimeoutMs)
	Ti.Enabled = False
	TT = ToastText
	CC = ClickCount
	BeforeCloseObj = CloseObject
	BeforeCloseEvent = CloseEvent
End Sub

Private Sub Ti_Tick
	I = 0
End Sub

Public Sub TapToClose
	Ti.Enabled = False
	Ti.Enabled = True
	ToastMessageShow (TT,False)
	I = I + 1
	If I >= CC Then
		Ti.Enabled = False
		If (BeforeCloseObj <> Null) And (BeforeCloseEvent<>"") Then CallSub(BeforeCloseObj,BeforeCloseEvent)
		ExitApplication
	End If
End Sub