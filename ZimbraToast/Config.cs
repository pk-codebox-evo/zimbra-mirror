﻿using System;
using System.IO;
using System.Xml;
using System.Drawing;
using System.Threading;
using System.Collections;
using System.Windows.Forms;
using System.ComponentModel;
using System.Runtime.InteropServices;
using Microsoft.Win32; //need it for mailto registration



namespace Zimbra.Toast
{
	/// <summary>
	/// The Config form is the main form of the application.
	/// </summary>
	public class Config : System.Windows.Forms.Form
	{
		private System.Windows.Forms.TabPage ConfigrationTabPage;
		private System.Windows.Forms.TabControl ConfigurationTabControl;
		private System.Windows.Forms.GroupBox ServerConnectionGroupBox;
		private System.Windows.Forms.Label ServerNameLabel;
		private System.Windows.Forms.CheckBox UseSecureConnectionCheckBox;
		private System.Windows.Forms.TextBox ServerNameTextBox;
		private System.Windows.Forms.NotifyIcon TrayIcon;
		private System.Windows.Forms.ContextMenu TrayMenu;
		private System.Windows.Forms.MenuItem ShowWindowMenuItem;
		private System.Windows.Forms.MenuItem ExitMenuItem;
		private System.Windows.Forms.MenuItem CheckNowMenuItem;
		private System.Windows.Forms.Button OK_Button;
		private System.ComponentModel.IContainer components;
		private System.Windows.Forms.GroupBox ZimbraAccountGroupBox;
		private System.Windows.Forms.TextBox AccountTextBox;
		private System.Windows.Forms.Label AccountLabel;
		private System.Windows.Forms.TextBox PasswordTextBox;
		private System.Windows.Forms.Label PasswordLabel;
		private System.Windows.Forms.TextBox VerifyPasswordTextBox;
		private System.Windows.Forms.Label VerifyPasswordLabel;
		private System.Windows.Forms.ToolTip DefaultToolTip;
		private System.Windows.Forms.TabPage AdvancedTabPage;
		private System.Windows.Forms.GroupBox groupBox2;
		private System.Windows.Forms.GroupBox AdvancedGroupBox;
		private System.Windows.Forms.Button RegisterMailto;
		private System.Windows.Forms.TextBox ClickURLPathFmtTextBox;
		private System.Windows.Forms.Label ClickURLLabel;
		private System.Windows.Forms.NumericUpDown PollingIntervalUpDown;
		private System.Windows.Forms.Label PollIntervalUnitsLabel;
		private System.Windows.Forms.Label PollIntervalLabel;
		
		private ToastConfig		toastConfig				= null;
		private ZimbraSession	zimbraSession			= null;
		private MailboxMonitor	mailboxMonitor			= null;
		
		private int				currentMsgIdx			= 0;
		private ToastForm		toastForm				= null;
		private AutoResetEvent	displayCompletionEvent	= null;
		private System.Windows.Forms.Button SoundFileBrowseButton;
		private System.Windows.Forms.TextBox SoundFileTextBox;
		private System.Windows.Forms.CheckBox PlaySoundCheckBox;
		private System.Windows.Forms.Button PlaySoundButton;

		private Zimbra.Client.MessageSummary[] msgSummaries = null;
		

		/// <summary>
		/// Default constructor
		/// </summary>
		public Config()
		{
			InitializeComponent();
		}


		/// <summary>
		/// Clean up any resources being used.
		/// </summary>
		protected override void Dispose( bool disposing )
		{
			if( disposing )
			{
				if(components != null)
				{
					components.Dispose();
				}
			}
			base.Dispose( disposing );
		}

		#region Windows Form Designer generated code
		/// <summary>
		/// Required method for Designer support - do not modify
		/// the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent()
		{
			this.components = new System.ComponentModel.Container();
			System.Resources.ResourceManager resources = new System.Resources.ResourceManager(typeof(Config));
			this.ConfigurationTabControl = new System.Windows.Forms.TabControl();
			this.ConfigrationTabPage = new System.Windows.Forms.TabPage();
			this.ZimbraAccountGroupBox = new System.Windows.Forms.GroupBox();
			this.VerifyPasswordTextBox = new System.Windows.Forms.TextBox();
			this.VerifyPasswordLabel = new System.Windows.Forms.Label();
			this.PasswordTextBox = new System.Windows.Forms.TextBox();
			this.PasswordLabel = new System.Windows.Forms.Label();
			this.AccountTextBox = new System.Windows.Forms.TextBox();
			this.AccountLabel = new System.Windows.Forms.Label();
			this.ServerConnectionGroupBox = new System.Windows.Forms.GroupBox();
			this.ServerNameTextBox = new System.Windows.Forms.TextBox();
			this.UseSecureConnectionCheckBox = new System.Windows.Forms.CheckBox();
			this.ServerNameLabel = new System.Windows.Forms.Label();
			this.OK_Button = new System.Windows.Forms.Button();
			this.TrayIcon = new System.Windows.Forms.NotifyIcon(this.components);
			this.TrayMenu = new System.Windows.Forms.ContextMenu();
			this.CheckNowMenuItem = new System.Windows.Forms.MenuItem();
			this.ShowWindowMenuItem = new System.Windows.Forms.MenuItem();
			this.ExitMenuItem = new System.Windows.Forms.MenuItem();
			this.DefaultToolTip = new System.Windows.Forms.ToolTip(this.components);
			this.AdvancedTabPage = new System.Windows.Forms.TabPage();
			this.groupBox2 = new System.Windows.Forms.GroupBox();
			this.SoundFileBrowseButton = new System.Windows.Forms.Button();
			this.SoundFileTextBox = new System.Windows.Forms.TextBox();
			this.PlaySoundCheckBox = new System.Windows.Forms.CheckBox();
			this.AdvancedGroupBox = new System.Windows.Forms.GroupBox();
			this.RegisterMailto = new System.Windows.Forms.Button();
			this.ClickURLPathFmtTextBox = new System.Windows.Forms.TextBox();
			this.ClickURLLabel = new System.Windows.Forms.Label();
			this.PollingIntervalUpDown = new System.Windows.Forms.NumericUpDown();
			this.PollIntervalUnitsLabel = new System.Windows.Forms.Label();
			this.PollIntervalLabel = new System.Windows.Forms.Label();
			this.PlaySoundButton = new System.Windows.Forms.Button();
			this.ConfigurationTabControl.SuspendLayout();
			this.ConfigrationTabPage.SuspendLayout();
			this.ZimbraAccountGroupBox.SuspendLayout();
			this.ServerConnectionGroupBox.SuspendLayout();
			this.AdvancedTabPage.SuspendLayout();
			this.groupBox2.SuspendLayout();
			this.AdvancedGroupBox.SuspendLayout();
			((System.ComponentModel.ISupportInitialize)(this.PollingIntervalUpDown)).BeginInit();
			this.SuspendLayout();
			// 
			// ConfigurationTabControl
			// 
			this.ConfigurationTabControl.Controls.Add(this.ConfigrationTabPage);
			this.ConfigurationTabControl.Controls.Add(this.AdvancedTabPage);
			this.ConfigurationTabControl.Location = new System.Drawing.Point(6, 8);
			this.ConfigurationTabControl.Name = "ConfigurationTabControl";
			this.ConfigurationTabControl.SelectedIndex = 0;
			this.ConfigurationTabControl.Size = new System.Drawing.Size(352, 288);
			this.ConfigurationTabControl.TabIndex = 0;
			this.ConfigurationTabControl.TabStop = false;
			// 
			// ConfigrationTabPage
			// 
			this.ConfigrationTabPage.BackColor = System.Drawing.SystemColors.ControlLightLight;
			this.ConfigrationTabPage.Controls.Add(this.ZimbraAccountGroupBox);
			this.ConfigrationTabPage.Controls.Add(this.ServerConnectionGroupBox);
			this.ConfigrationTabPage.Location = new System.Drawing.Point(4, 22);
			this.ConfigrationTabPage.Name = "ConfigrationTabPage";
			this.ConfigrationTabPage.Size = new System.Drawing.Size(344, 262);
			this.ConfigrationTabPage.TabIndex = 0;
			this.ConfigrationTabPage.Text = "Configuration";
			// 
			// ZimbraAccountGroupBox
			// 
			this.ZimbraAccountGroupBox.Controls.Add(this.VerifyPasswordTextBox);
			this.ZimbraAccountGroupBox.Controls.Add(this.VerifyPasswordLabel);
			this.ZimbraAccountGroupBox.Controls.Add(this.PasswordTextBox);
			this.ZimbraAccountGroupBox.Controls.Add(this.PasswordLabel);
			this.ZimbraAccountGroupBox.Controls.Add(this.AccountTextBox);
			this.ZimbraAccountGroupBox.Controls.Add(this.AccountLabel);
			this.ZimbraAccountGroupBox.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.ZimbraAccountGroupBox.Location = new System.Drawing.Point(12, 96);
			this.ZimbraAccountGroupBox.Name = "ZimbraAccountGroupBox";
			this.ZimbraAccountGroupBox.Size = new System.Drawing.Size(316, 110);
			this.ZimbraAccountGroupBox.TabIndex = 1;
			this.ZimbraAccountGroupBox.TabStop = false;
			this.ZimbraAccountGroupBox.Text = "Zimbra Account";
			// 
			// VerifyPasswordTextBox
			// 
			this.VerifyPasswordTextBox.Location = new System.Drawing.Point(102, 74);
			this.VerifyPasswordTextBox.Name = "VerifyPasswordTextBox";
			this.VerifyPasswordTextBox.PasswordChar = '●';
			this.VerifyPasswordTextBox.Size = new System.Drawing.Size(202, 20);
			this.VerifyPasswordTextBox.TabIndex = 7;
			this.VerifyPasswordTextBox.Text = "";
			this.DefaultToolTip.SetToolTip(this.VerifyPasswordTextBox, "Your Zimbra account password.");
			this.VerifyPasswordTextBox.KeyUp += new System.Windows.Forms.KeyEventHandler(this.Configuration_TextFieldKeyPress);
			// 
			// VerifyPasswordLabel
			// 
			this.VerifyPasswordLabel.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.VerifyPasswordLabel.ImageAlign = System.Drawing.ContentAlignment.MiddleRight;
			this.VerifyPasswordLabel.Location = new System.Drawing.Point(10, 76);
			this.VerifyPasswordLabel.Name = "VerifyPasswordLabel";
			this.VerifyPasswordLabel.Size = new System.Drawing.Size(84, 16);
			this.VerifyPasswordLabel.TabIndex = 6;
			this.VerifyPasswordLabel.Text = "Verify Password";
			this.VerifyPasswordLabel.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
			// 
			// PasswordTextBox
			// 
			this.PasswordTextBox.Location = new System.Drawing.Point(102, 48);
			this.PasswordTextBox.Name = "PasswordTextBox";
			this.PasswordTextBox.PasswordChar = '●';
			this.PasswordTextBox.Size = new System.Drawing.Size(202, 20);
			this.PasswordTextBox.TabIndex = 5;
			this.PasswordTextBox.Text = "";
			this.DefaultToolTip.SetToolTip(this.PasswordTextBox, "Your Zimbra account password.");
			this.PasswordTextBox.KeyUp += new System.Windows.Forms.KeyEventHandler(this.Configuration_TextFieldKeyPress);
			// 
			// PasswordLabel
			// 
			this.PasswordLabel.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.PasswordLabel.ImageAlign = System.Drawing.ContentAlignment.MiddleRight;
			this.PasswordLabel.Location = new System.Drawing.Point(10, 50);
			this.PasswordLabel.Name = "PasswordLabel";
			this.PasswordLabel.Size = new System.Drawing.Size(84, 16);
			this.PasswordLabel.TabIndex = 4;
			this.PasswordLabel.Text = "Password";
			this.PasswordLabel.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
			// 
			// AccountTextBox
			// 
			this.AccountTextBox.Location = new System.Drawing.Point(102, 22);
			this.AccountTextBox.Name = "AccountTextBox";
			this.AccountTextBox.Size = new System.Drawing.Size(202, 20);
			this.AccountTextBox.TabIndex = 3;
			this.AccountTextBox.Text = "";
			this.DefaultToolTip.SetToolTip(this.AccountTextBox, "Your Zimbra account name.  Example me@zimbra.company.com");
			this.AccountTextBox.KeyUp += new System.Windows.Forms.KeyEventHandler(this.Configuration_TextFieldKeyPress);
			// 
			// AccountLabel
			// 
			this.AccountLabel.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.AccountLabel.ImageAlign = System.Drawing.ContentAlignment.MiddleRight;
			this.AccountLabel.Location = new System.Drawing.Point(10, 24);
			this.AccountLabel.Name = "AccountLabel";
			this.AccountLabel.Size = new System.Drawing.Size(84, 16);
			this.AccountLabel.TabIndex = 2;
			this.AccountLabel.Text = "Account";
			this.AccountLabel.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
			// 
			// ServerConnectionGroupBox
			// 
			this.ServerConnectionGroupBox.Controls.Add(this.ServerNameTextBox);
			this.ServerConnectionGroupBox.Controls.Add(this.UseSecureConnectionCheckBox);
			this.ServerConnectionGroupBox.Controls.Add(this.ServerNameLabel);
			this.ServerConnectionGroupBox.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.ServerConnectionGroupBox.Location = new System.Drawing.Point(12, 12);
			this.ServerConnectionGroupBox.Name = "ServerConnectionGroupBox";
			this.ServerConnectionGroupBox.Size = new System.Drawing.Size(316, 78);
			this.ServerConnectionGroupBox.TabIndex = 0;
			this.ServerConnectionGroupBox.TabStop = false;
			this.ServerConnectionGroupBox.Text = "Zimbra Server Connection";
			// 
			// ServerNameTextBox
			// 
			this.ServerNameTextBox.Location = new System.Drawing.Point(102, 23);
			this.ServerNameTextBox.Name = "ServerNameTextBox";
			this.ServerNameTextBox.Size = new System.Drawing.Size(202, 20);
			this.ServerNameTextBox.TabIndex = 1;
			this.ServerNameTextBox.Text = "";
			this.DefaultToolTip.SetToolTip(this.ServerNameTextBox, "The name of yoru Zimbra server. Example zimbra.company.com");
			this.ServerNameTextBox.KeyUp += new System.Windows.Forms.KeyEventHandler(this.Configuration_TextFieldKeyPress);
			// 
			// UseSecureConnectionCheckBox
			// 
			this.UseSecureConnectionCheckBox.Checked = true;
			this.UseSecureConnectionCheckBox.CheckState = System.Windows.Forms.CheckState.Checked;
			this.UseSecureConnectionCheckBox.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.UseSecureConnectionCheckBox.Location = new System.Drawing.Point(102, 48);
			this.UseSecureConnectionCheckBox.Name = "UseSecureConnectionCheckBox";
			this.UseSecureConnectionCheckBox.Size = new System.Drawing.Size(182, 18);
			this.UseSecureConnectionCheckBox.TabIndex = 2;
			this.UseSecureConnectionCheckBox.Text = "Use Secure Connection";
			this.DefaultToolTip.SetToolTip(this.UseSecureConnectionCheckBox, "Use a secure connection when communication with your Zimbra server");
			// 
			// ServerNameLabel
			// 
			this.ServerNameLabel.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.ServerNameLabel.ImageAlign = System.Drawing.ContentAlignment.MiddleRight;
			this.ServerNameLabel.Location = new System.Drawing.Point(10, 26);
			this.ServerNameLabel.Name = "ServerNameLabel";
			this.ServerNameLabel.Size = new System.Drawing.Size(84, 16);
			this.ServerNameLabel.TabIndex = 0;
			this.ServerNameLabel.Text = "Server Name";
			this.ServerNameLabel.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
			// 
			// OK_Button
			// 
			this.OK_Button.DialogResult = System.Windows.Forms.DialogResult.OK;
			this.OK_Button.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.OK_Button.Location = new System.Drawing.Point(283, 302);
			this.OK_Button.Name = "OK_Button";
			this.OK_Button.TabIndex = 1;
			this.OK_Button.Text = "OK";
			this.OK_Button.Click += new System.EventHandler(this.OKButton_Click);
			// 
			// TrayIcon
			// 
			this.TrayIcon.ContextMenu = this.TrayMenu;
			this.TrayIcon.Icon = ((System.Drawing.Icon)(resources.GetObject("TrayIcon.Icon")));
			this.TrayIcon.Text = "Zimbra Toaster";
			this.TrayIcon.Visible = true;
			this.TrayIcon.DoubleClick += new System.EventHandler(this.TrayIcon_DoubleClick);
			// 
			// TrayMenu
			// 
			this.TrayMenu.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
																					 this.CheckNowMenuItem,
																					 this.ShowWindowMenuItem,
																					 this.ExitMenuItem});
			// 
			// CheckNowMenuItem
			// 
			this.CheckNowMenuItem.Index = 0;
			this.CheckNowMenuItem.Text = "Check For New Mail";
			this.CheckNowMenuItem.Click += new System.EventHandler(this.CheckNowMenuItem_Click);
			// 
			// ShowWindowMenuItem
			// 
			this.ShowWindowMenuItem.Index = 1;
			this.ShowWindowMenuItem.Text = "Settings";
			this.ShowWindowMenuItem.Click += new System.EventHandler(this.ShowWindowMenuItem_Click);
			// 
			// ExitMenuItem
			// 
			this.ExitMenuItem.Index = 2;
			this.ExitMenuItem.Text = "Exit";
			this.ExitMenuItem.Click += new System.EventHandler(this.ExitMenuItem_Click);
			// 
			// AdvancedTabPage
			// 
			this.AdvancedTabPage.BackColor = System.Drawing.SystemColors.ControlLightLight;
			this.AdvancedTabPage.Controls.Add(this.AdvancedGroupBox);
			this.AdvancedTabPage.Controls.Add(this.groupBox2);
			this.AdvancedTabPage.Location = new System.Drawing.Point(4, 22);
			this.AdvancedTabPage.Name = "AdvancedTabPage";
			this.AdvancedTabPage.Size = new System.Drawing.Size(344, 262);
			this.AdvancedTabPage.TabIndex = 1;
			this.AdvancedTabPage.Text = "Advanced";
			// 
			// groupBox2
			// 
			this.groupBox2.Controls.Add(this.PlaySoundButton);
			this.groupBox2.Controls.Add(this.PlaySoundCheckBox);
			this.groupBox2.Controls.Add(this.SoundFileBrowseButton);
			this.groupBox2.Controls.Add(this.SoundFileTextBox);
			this.groupBox2.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.groupBox2.Location = new System.Drawing.Point(12, 130);
			this.groupBox2.Name = "groupBox2";
			this.groupBox2.Size = new System.Drawing.Size(316, 110);
			this.groupBox2.TabIndex = 4;
			this.groupBox2.TabStop = false;
			this.groupBox2.Text = "Sound";
			// 
			// SoundFileBrowseButton
			// 
			this.SoundFileBrowseButton.BackColor = System.Drawing.SystemColors.Control;
			this.SoundFileBrowseButton.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.SoundFileBrowseButton.Location = new System.Drawing.Point(220, 72);
			this.SoundFileBrowseButton.Name = "SoundFileBrowseButton";
			this.SoundFileBrowseButton.Size = new System.Drawing.Size(80, 23);
			this.SoundFileBrowseButton.TabIndex = 13;
			this.SoundFileBrowseButton.Text = "Browse";
			this.SoundFileBrowseButton.Click += new System.EventHandler(this.SoundFileBrowseButton_Click);
			// 
			// SoundFileTextBox
			// 
			this.SoundFileTextBox.Location = new System.Drawing.Point(16, 46);
			this.SoundFileTextBox.Name = "SoundFileTextBox";
			this.SoundFileTextBox.ReadOnly = true;
			this.SoundFileTextBox.Size = new System.Drawing.Size(284, 20);
			this.SoundFileTextBox.TabIndex = 12;
			this.SoundFileTextBox.Text = "";
			this.DefaultToolTip.SetToolTip(this.SoundFileTextBox, "Path portion of the URL to open when an item is clicked. Use {0} to represent the" +
				" items id.");
			// 
			// PlaySoundCheckBox
			// 
			this.PlaySoundCheckBox.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.PlaySoundCheckBox.Location = new System.Drawing.Point(16, 24);
			this.PlaySoundCheckBox.Name = "PlaySoundCheckBox";
			this.PlaySoundCheckBox.Size = new System.Drawing.Size(254, 18);
			this.PlaySoundCheckBox.TabIndex = 14;
			this.PlaySoundCheckBox.Text = "Play sound when new messages arive";
			this.PlaySoundCheckBox.Click += new System.EventHandler(this.PlaySoundCheckBox_Click);
			// 
			// AdvancedGroupBox
			// 
			this.AdvancedGroupBox.BackColor = System.Drawing.SystemColors.ControlLightLight;
			this.AdvancedGroupBox.Controls.Add(this.RegisterMailto);
			this.AdvancedGroupBox.Controls.Add(this.ClickURLPathFmtTextBox);
			this.AdvancedGroupBox.Controls.Add(this.ClickURLLabel);
			this.AdvancedGroupBox.Controls.Add(this.PollingIntervalUpDown);
			this.AdvancedGroupBox.Controls.Add(this.PollIntervalUnitsLabel);
			this.AdvancedGroupBox.Controls.Add(this.PollIntervalLabel);
			this.AdvancedGroupBox.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.AdvancedGroupBox.Location = new System.Drawing.Point(12, 12);
			this.AdvancedGroupBox.Name = "AdvancedGroupBox";
			this.AdvancedGroupBox.Size = new System.Drawing.Size(316, 112);
			this.AdvancedGroupBox.TabIndex = 5;
			this.AdvancedGroupBox.TabStop = false;
			this.AdvancedGroupBox.Text = "Advanced";
			// 
			// RegisterMailto
			// 
			this.RegisterMailto.BackColor = System.Drawing.SystemColors.Control;
			this.RegisterMailto.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.RegisterMailto.Location = new System.Drawing.Point(102, 76);
			this.RegisterMailto.Name = "RegisterMailto";
			this.RegisterMailto.Size = new System.Drawing.Size(202, 23);
			this.RegisterMailto.TabIndex = 10;
			this.RegisterMailto.Text = "Register Mailto Handler";
			// 
			// ClickURLPathFmtTextBox
			// 
			this.ClickURLPathFmtTextBox.Location = new System.Drawing.Point(102, 48);
			this.ClickURLPathFmtTextBox.Name = "ClickURLPathFmtTextBox";
			this.ClickURLPathFmtTextBox.Size = new System.Drawing.Size(202, 20);
			this.ClickURLPathFmtTextBox.TabIndex = 8;
			this.ClickURLPathFmtTextBox.Text = "";
			this.DefaultToolTip.SetToolTip(this.ClickURLPathFmtTextBox, "Path portion of the URL to open when an item is clicked. Use {0} to represent the" +
				" items id.");
			// 
			// ClickURLLabel
			// 
			this.ClickURLLabel.BackColor = System.Drawing.SystemColors.ControlLightLight;
			this.ClickURLLabel.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.ClickURLLabel.ImageAlign = System.Drawing.ContentAlignment.MiddleRight;
			this.ClickURLLabel.Location = new System.Drawing.Point(10, 50);
			this.ClickURLLabel.Name = "ClickURLLabel";
			this.ClickURLLabel.Size = new System.Drawing.Size(84, 16);
			this.ClickURLLabel.TabIndex = 6;
			this.ClickURLLabel.Text = "Click Item URL";
			this.ClickURLLabel.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
			// 
			// PollingIntervalUpDown
			// 
			this.PollingIntervalUpDown.Location = new System.Drawing.Point(102, 22);
			this.PollingIntervalUpDown.Maximum = new System.Decimal(new int[] {
																				  30,
																				  0,
																				  0,
																				  0});
			this.PollingIntervalUpDown.Minimum = new System.Decimal(new int[] {
																				  1,
																				  0,
																				  0,
																				  0});
			this.PollingIntervalUpDown.Name = "PollingIntervalUpDown";
			this.PollingIntervalUpDown.Size = new System.Drawing.Size(46, 20);
			this.PollingIntervalUpDown.TabIndex = 5;
			this.PollingIntervalUpDown.TextAlign = System.Windows.Forms.HorizontalAlignment.Center;
			this.DefaultToolTip.SetToolTip(this.PollingIntervalUpDown, "How often to check for new messages.");
			this.PollingIntervalUpDown.Value = new System.Decimal(new int[] {
																				1,
																				0,
																				0,
																				0});
			// 
			// PollIntervalUnitsLabel
			// 
			this.PollIntervalUnitsLabel.BackColor = System.Drawing.SystemColors.ControlLightLight;
			this.PollIntervalUnitsLabel.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.PollIntervalUnitsLabel.ImageAlign = System.Drawing.ContentAlignment.MiddleRight;
			this.PollIntervalUnitsLabel.Location = new System.Drawing.Point(156, 24);
			this.PollIntervalUnitsLabel.Name = "PollIntervalUnitsLabel";
			this.PollIntervalUnitsLabel.Size = new System.Drawing.Size(84, 16);
			this.PollIntervalUnitsLabel.TabIndex = 4;
			this.PollIntervalUnitsLabel.Text = "Minutes";
			this.PollIntervalUnitsLabel.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
			// 
			// PollIntervalLabel
			// 
			this.PollIntervalLabel.BackColor = System.Drawing.SystemColors.ControlLightLight;
			this.PollIntervalLabel.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.PollIntervalLabel.ImageAlign = System.Drawing.ContentAlignment.MiddleRight;
			this.PollIntervalLabel.Location = new System.Drawing.Point(10, 24);
			this.PollIntervalLabel.Name = "PollIntervalLabel";
			this.PollIntervalLabel.Size = new System.Drawing.Size(84, 16);
			this.PollIntervalLabel.TabIndex = 2;
			this.PollIntervalLabel.Text = "Poll Interval";
			this.PollIntervalLabel.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
			// 
			// PlaySoundButton
			// 
			this.PlaySoundButton.BackColor = System.Drawing.SystemColors.Control;
			this.PlaySoundButton.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.PlaySoundButton.Location = new System.Drawing.Point(132, 72);
			this.PlaySoundButton.Name = "PlaySoundButton";
			this.PlaySoundButton.Size = new System.Drawing.Size(80, 23);
			this.PlaySoundButton.TabIndex = 15;
			this.PlaySoundButton.Text = "Play Sound";
			this.PlaySoundButton.Click += new System.EventHandler(this.PlaySoundButton_Click);
			// 
			// Config
			// 
			this.AcceptButton = this.OK_Button;
			this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
			this.BackColor = System.Drawing.SystemColors.Control;
			this.ClientSize = new System.Drawing.Size(364, 333);
			this.ControlBox = false;
			this.Controls.Add(this.OK_Button);
			this.Controls.Add(this.ConfigurationTabControl);
			this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
			this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
			this.MaximizeBox = false;
			this.MinimizeBox = false;
			this.Name = "Config";
			this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
			this.Text = "Zimbra Toaster Configuration";
			this.Resize += new System.EventHandler(this.Config_Resize);
			this.Closing += new System.ComponentModel.CancelEventHandler(this.Config_Closing);
			this.Load += new System.EventHandler(this.Config_Load);
			this.ConfigurationTabControl.ResumeLayout(false);
			this.ConfigrationTabPage.ResumeLayout(false);
			this.ZimbraAccountGroupBox.ResumeLayout(false);
			this.ServerConnectionGroupBox.ResumeLayout(false);
			this.AdvancedTabPage.ResumeLayout(false);
			this.groupBox2.ResumeLayout(false);
			this.AdvancedGroupBox.ResumeLayout(false);
			((System.ComponentModel.ISupportInitialize)(this.PollingIntervalUpDown)).EndInit();
			this.ResumeLayout(false);

		}
		#endregion

		#region Tray Menu Event Handlers
		/// <summary>
		/// TrayIcon was double clicked - show the config window
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void TrayIcon_DoubleClick(object sender, System.EventArgs e)
		{
			mailboxMonitor.CheckMailbox();
		}


		/// <summary>
		/// Show Window was selected from the tray menu
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void ShowWindowMenuItem_Click(object sender, System.EventArgs e)
		{
			Show();
			WindowState = FormWindowState.Normal;
		}

		/// <summary>
		/// Exit was selected from the tray menu
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void ExitMenuItem_Click(object sender, System.EventArgs e)
		{
			Close();
		}

		/// <summary>
		/// Show Toast was selecte from the menu
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void ShowToastMenuItem_Click(object sender, System.EventArgs e)
		{
			//this.toaster.Show();
		}

		/// <summary>
		/// Check for new stuff right now
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void CheckNowMenuItem_Click(object sender, System.EventArgs e)
		{
			mailboxMonitor.CheckMailbox();
		}
		#endregion

		#region Form Control Event Handlers

		/// <summary>
		/// Config was resized - used to know when the window has been minimized
		/// so we can hide it from the user
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void Config_Resize(object sender, System.EventArgs e)
		{
			if (FormWindowState.Minimized == WindowState) 
			{
				Hide();
			}
		}


		/// <summary>
		/// OK was clicked in the config form - serialize the params and hide the window
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void OKButton_Click(object sender, System.EventArgs e)
		{
			Hide();
			
			if( mailboxMonitor != null )
				mailboxMonitor.StopMonitoring();

			//this updates both toastConfig and zimbraSession
			SaveParams();
			
			mailboxMonitor.Update( zimbraSession, toastConfig.PollInterval );

			//start monitoring
			mailboxMonitor.StartMonitoring();
		}


		/// <summary>
		/// A key was pressed in one of the config text fields.  Figure out if we can 
		/// the enabled state of the OK button
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void Configuration_TextFieldKeyPress(object sender, System.Windows.Forms.KeyEventArgs e)		
		{
			UpdateOkButton();
		}

		/// <summary>
		/// One time initializations
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void Config_Load(object sender, System.EventArgs e)
		{
			//load the params from the file - defaults used if file doesn't exist
			toastConfig = new ToastConfig();

			//create the zimbra session
			UpdateZimbraSession();
							
			//initialize the mailbox monitor
			mailboxMonitor = new MailboxMonitor( zimbraSession, toastConfig.PollInterval );

			//the toaster needs to know when new msgs arrive 
			mailboxMonitor.OnNewMsgs += new Zimbra.Toast.MailboxMonitor.NewMsgHandler(DisplayNewMessages);

			//set the tf values
			InitDialogFields();
			
			UpdateOkButton();
		}
		
		
		/// <summary>
		/// Form is closing, shut everything down
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void Config_Closing(object sender, System.ComponentModel.CancelEventArgs e)
		{
			if( mailboxMonitor != null )
				mailboxMonitor.StopMonitoring();
			SaveParams();
		}


		/// <summary>
		/// use ZWC as the default mailto handler
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void RegisterMailto_Click(object sender, System.EventArgs e)
		{
			String openCmd = "\"" + System.Windows.Forms.Application.ExecutablePath + "\" \"%1\"";
			RegistryKey key = Registry.LocalMachine.OpenSubKey( @"software\classes\mailto\shell\open\command", true );
			key.SetValue( "", openCmd );

			RegistryKey zimbraKey = Registry.LocalMachine.CreateSubKey( @"Software\Clients\Mail\Zimbra" );
			zimbraKey.SetValue( "", "Zimbra" );
			
			key = zimbraKey.CreateSubKey( @"Protocols\mailto\shell\open\command" );
			key.SetValue( "", openCmd );

			key = zimbraKey.CreateSubKey( @"Shell\open\command" );
			key.SetValue( "", openCmd );

			System.Windows.Forms.MessageBox.Show( 
				"Registered Successfully", 
				"Register Zimbra As Mailto Handler", 
				System.Windows.Forms.MessageBoxButtons.OK, 
				System.Windows.Forms.MessageBoxIcon.Information );
		}


		/// <summary>
		/// 
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void PlaySoundCheckBox_Click(object sender, System.EventArgs e)
		{
			UpdateOkButton();
		}


		/// <summary>
		/// 
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void SoundFileBrowseButton_Click(object sender, System.EventArgs e)
		{
			OpenFileDialog f = new OpenFileDialog();
			f.Multiselect = false;
			f.ShowReadOnly = false;
			f.CheckFileExists = true;
			f.Filter = "Wav files (*.wav)|*.wav|All files (*.*)|*.*";
			f.Title = "Select sound file";
			f.ValidateNames = true;
			f.RestoreDirectory = false;
			f.ShowHelp = false;
			f.InitialDirectory = System.Environment.SystemDirectory + @"\..\Media";

			DialogResult dr = f.ShowDialog();

			if( dr == DialogResult.OK )
			{
				this.SoundFileTextBox.Text = f.FileName;
			}

			UpdateOkButton();
		}

		/// <summary>
		/// Play the sound file
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void PlaySoundButton_Click(object sender, System.EventArgs e)
		{
			PlaySound( this.SoundFileTextBox.Text, IntPtr.Zero, 
				SoundFlags.SND_FILENAME | SoundFlags.SND_ASYNC );
		}

		

		#endregion Form Control Event Handlers

		#region Private helpers

		/// <summary>
		/// Based on the current values of all the text fields set
		/// the enabled state of the OK button.  It should only be
		/// enabled if all fields have what look like valid values.
		/// </summary>
		private void UpdateOkButton()
		{
			bool bAllValues = (
				TfHasValue( this.ServerNameTextBox ) &&
				TfHasValue( this.AccountTextBox ) &&
				TfHasValue( this.PasswordTextBox ) &&
				TfHasValue( this.VerifyPasswordTextBox ) &&
				TfHasValue( this.ClickURLPathFmtTextBox ) );

			bool bSoundValues = (this.PlaySoundCheckBox.Checked && TfHasValue(this.SoundFileTextBox ) ) ||
				!this.PlaySoundCheckBox.Checked;

			bool bMatch = false;
			if( TfHasValue(PasswordTextBox) && TfHasValue(VerifyPasswordTextBox)) 
			{
				bMatch = this.PasswordTextBox.Text.Equals( this.VerifyPasswordTextBox.Text );
			}
			
			this.PlaySoundButton.Enabled = TfHasValue( this.SoundFileTextBox ) && this.PlaySoundCheckBox.Checked;

			this.SoundFileBrowseButton.Enabled = this.PlaySoundCheckBox.Checked;
			
			this.OK_Button.Enabled = bAllValues && bMatch && bSoundValues;
			
			this.RegisterMailto.Enabled = bAllValues && bMatch;
		}


		/// <summary>
		/// Helper to determine if the text box contains a string
		/// </summary>
		/// <param name="t">The text box</param>
		/// <returns>True if t contains a string of length > 0</returns>
		private static bool TfHasValue( TextBox t )
		{
			return (t != null && t.Text != null && t.Text.Length > 0 );
		}


		/// <summary>
		/// Is the toaster fully configured
		/// </summary>
		private bool Configured 
		{
			get{ return toastConfig != null; }
		}


		/// <summary>
		/// Serialize the toaster configuration to an xml file
		/// </summary>
		private void SaveParams()
		{
			UpdateToastConfig();
			toastConfig.Save();
		}


		/// <summary>
		/// Update the toast config based on whats in the UI
		/// </summary>
		private void UpdateToastConfig()
		{
			toastConfig = new ToastConfig( 
				this.ServerNameTextBox.Text,
				this.UseSecureConnectionCheckBox.Checked,
				this.AccountTextBox.Text,
				this.PasswordTextBox.Text,
				(ushort)this.PollingIntervalUpDown.Value,
				this.ClickURLPathFmtTextBox.Text,
				toastConfig.Location,
				this.SoundFileTextBox.Text );

			//any time we update the toast config we should update the zimbra session
			UpdateZimbraSession();
		}


		/// <summary>
		/// update thee zimbra session with whats in the toast config
		/// </summary>
		private void UpdateZimbraSession()
		{
			zimbraSession = new ZimbraSession(
					toastConfig.Account, 
					toastConfig.Password,
					toastConfig.Server,
					toastConfig.Port,
					toastConfig.UseSecure );
		}

		/// <summary>
		/// Populate the dialog text fields with the values loaded in toastConfig
		/// </summary>
		private void InitDialogFields()
		{
			if( toastConfig.Port != 80 && toastConfig.Port != 443 ) 
			{
				this.ServerNameTextBox.Text = toastConfig.Server + ":" + toastConfig.Port;
			}
			else
			{
				this.ServerNameTextBox.Text = toastConfig.Server;
			}

			this.UseSecureConnectionCheckBox.Checked = toastConfig.UseSecure;
			this.AccountTextBox.Text = toastConfig.Account;
			this.PasswordTextBox.Text = toastConfig.Password;
			this.VerifyPasswordTextBox.Text = toastConfig.Password;
			this.PollingIntervalUpDown.Value = toastConfig.PollInterval;
			this.ClickURLPathFmtTextBox.Text = toastConfig.ClickURLPathFmt;

			if( toastConfig.SoundFile != null ) 
			{
				this.PlaySoundCheckBox.Checked = true;
				this.SoundFileTextBox.Text = toastConfig.SoundFile;
			}
		}

		#endregion

		#region Toaser event handlers

		/// <summary>
		/// Open the item in a browser
		/// </summary>
		/// <param name="itemId"></param>
		private void OpenItem( String itemId )
		{
			//open the uri in the default browsers
			String uri = toastConfig.GetItemUri( itemId );

			//this could be dangerous, but at lease we are
			//assured it starts with "http"
			System.Diagnostics.Process.Start( uri );
		}

		/// <summary>
		/// Tell Zimbra to flag the item
		/// </summary>
		/// <param name="itemId"></param>
		private void FlagItem( String itemId )
		{
			//for now do it in the UI thread to block the UI
			zimbraSession.FlagItem(itemId);
		}

		/// <summary>
		/// Tell zimbra to move the item to the trash folder
		/// </summary>
		/// <param name="itemId"></param>
		private void DeleteItem( String itemId )
		{
			zimbraSession.MoveItem( itemId, "3" );
		}

		#endregion

		#region handle display of msg summaries (cycle toaster)
		/// <summary>
		/// This should be called in the mailboxmonitors worker thread
		/// the idea is to hold it up while toast is being displayed so
		/// it doesn't poll the server and obtain a new MessageSummary[]
		/// </summary>
		/// <param name="msgs">the new messages on the server</param>
		/// <param name="are">the event to signal once all msgs have been displayed</param>
		public void DisplayNewMessages( Zimbra.Client.MessageSummary[] msgs, AutoResetEvent are )
		{
			//nothing to display, signal the event and bail
			if( msgs == null || msgs.Length == 0 ) 
			{
				are.Set();
				return;
			}

			//play a sound?
			if( toastConfig.SoundFile != null ) 
			{
				PlaySound( toastConfig.SoundFile, IntPtr.Zero, 
					SoundFlags.SND_ASYNC | SoundFlags.SND_FILENAME );
			}

			//setup the state to cycle through the message summaries
			this.currentMsgIdx = 0;
			this.msgSummaries = msgs;
			this.displayCompletionEvent = are;

			//start it
			Invoke( new MethodInvoker(ShowCurrentMessagSummary) );
		}


		/// <summary>
		/// Display the current message summary in a new piece of toast
		/// assumes the previous toast has been closed
		/// </summary>
		private void ShowCurrentMessagSummary()
		{
			toastForm = new ToastForm(this.msgSummaries[this.currentMsgIdx++]);
			toastForm.Location = toastConfig.Location;
			toastForm.Closed += new EventHandler(ToasterClosed);
			toastForm.OnOpenItem += new ToastForm.OpenItemHandler(OpenItem);
			toastForm.OnFlagItem += new ToastForm.FlagItemHandler(FlagItem);
			toastForm.OnDeleteItem += new ToastForm.DeleteItemHandler(DeleteItem);
			toastForm.Show();
		}


		/// <summary>
		/// Handle 'Closed' event fired from a ToastForm
		/// updates the current msg summary idx and fires off the next toast
		/// if all msg summaries displayed, signal mailboxMonitor
		/// </summary>
		/// <param name="o"></param>
		/// <param name="a"></param>
		private void ToasterClosed(object o, EventArgs a)
		{
			toastConfig.Location = toastForm.Location;
			if( this.currentMsgIdx < this.msgSummaries.Length ) 
			{
				ShowCurrentMessagSummary();
			}
			else
			{
				this.displayCompletionEvent.Set();
			}
		}
		#endregion


		#region  Helper to play sound
		// PlaySound()
		[DllImport("winmm.dll", SetLastError=true, 
			 CallingConvention=CallingConvention.Winapi)]
		static extern bool PlaySound(
			string pszSound,
			IntPtr hMod,
			SoundFlags sf );

		// Flags for playing sounds.  For this example, we are reading 
		// the sound from a filename, so we need only specify 
		// SND_FILENAME | SND_ASYNC
		[Flags]
		public enum SoundFlags : int 
		{
			SND_SYNC = 0x0000,  // play synchronously (default) 
			SND_ASYNC = 0x0001,  // play asynchronously 
			SND_NODEFAULT = 0x0002,  // silence (!default) if sound not found 
			SND_MEMORY = 0x0004,  // pszSound points to a memory file
			SND_LOOP = 0x0008,  // loop the sound until next sndPlaySound 
			SND_NOSTOP = 0x0010,  // don't stop any currently playing sound 
			SND_NOWAIT = 0x00002000, // don't wait if the driver is busy 
			SND_ALIAS = 0x00010000, // name is a registry alias 
			SND_ALIAS_ID = 0x00110000, // alias is a predefined ID
			SND_FILENAME = 0x00020000, // name is file name 
			SND_RESOURCE = 0x00040004  // name is resource name or atom 
		}
		#endregion


	}







	/// <summary>
	/// The toaster configuration - server, port, acct, etc
	/// </summary>
	public class ToastConfig
	{
		//the server name (can end in :<port>)
		private String	server;

		//use a secure connection or clear?
		private bool	useSecure = true;

		//the zimbra account to monitor
		private String	account;

		//the password of the zimbra account
		private String	password;

		//how often to hit the server and check for new messages
		private UInt16	pollInterval = 1;

		//path to something on the server
		private String	clickURLPathFmt = DEFAULT_CLICK_URL_PATH_FMT;

		//the top left of the toaster window
		private Point	location;

		//the sound to play when new msgs arrive
		private String	soundFile;

		//the filename of the configuration file
		private static String filename = "ztoastcfg.xml";

		//key that identifies the password in the password db
		private static String PASSWORD_KEY = "ZT0A5T";

		//default polling interval is 5 minutes
		private static UInt16 DEFAULT_POLL_INTERVAL = 5;

		//default click-url path format specifier
		private static String DEFAULT_CLICK_URL_PATH_FMT = "/zimbra/?view=msg&id={0}";

		//TODO: request a REST api for this
		//private static String DEFAULT_CLICK_URL_PATH_FMT = "/service/home/~/?fmt=html&id={0}";


		private static String CFG_ZIMBRA_TOAST	= "ZimbraToast";
		private static String CFG_SERVER		= "Server";
		private static String CFG_USE_SECURE	= "UseSecure";
		private static String CFG_ACCOUNT		= "Account";
		private static String CFG_PASSWORD		= "EncryptedPassword";
		private static String CFG_POLL_INTERVAL = "PollInterval";
		private static String CFG_CLICK_URL		= "ClickURLPathFmt";
		private static String CFG_LOCATION		= "WindowLocation";
		private static String CFG_SOUND_FILE	= "SoundFile";
		

		/// <summary>
		/// Default constructor - attempts to read the parameters from 
		/// configuration file.  It will throw if it has any problems.
		/// </summary>
		public ToastConfig()
		{
			XmlDocument doc = new XmlDocument();
			try
			{
				doc.Load(ParamFilename());
			}
			catch( Exception )
			{
			}

			server			= GetConfigParamString( doc, CFG_ZIMBRA_TOAST + "/" + CFG_SERVER, null );
			useSecure		= GetConfigParamBool  ( doc, CFG_ZIMBRA_TOAST + "/" + CFG_USE_SECURE, true );
			account			= GetConfigParamString( doc, CFG_ZIMBRA_TOAST + "/" + CFG_ACCOUNT, null );
			password		= GetConfigParamPassword( doc, CFG_ZIMBRA_TOAST + "/" + CFG_PASSWORD, null );
			pollInterval	= GetConfigParamUInt16( doc, CFG_ZIMBRA_TOAST + "/" + CFG_POLL_INTERVAL, DEFAULT_POLL_INTERVAL );
			clickURLPathFmt = GetConfigParamString( doc, CFG_ZIMBRA_TOAST + "/" + CFG_CLICK_URL, DEFAULT_CLICK_URL_PATH_FMT );
			location		= GetConfigParamPoint ( doc, CFG_ZIMBRA_TOAST + "/" + CFG_LOCATION, new Point( 100, 100 ) );
			soundFile		= GetConfigParamString( doc, CFG_ZIMBRA_TOAST + "/" + CFG_SOUND_FILE, null );
		}


		/// <summary>
		/// Return a string configuration parameter from the xml config file
		/// </summary>
		/// <param name="doc">the config file</param>
		/// <param name="xpath">the node</param>
		/// <returns>the string value of the param, or null if it doesnt exist</returns>
		private String GetConfigParamString( XmlDocument doc, String xpath, String strDefault )
		{
			XmlNode n = doc.SelectSingleNode( xpath );
			try 
			{
				return n.InnerText;
			}
			catch( Exception )
			{
				return strDefault;
			}
		}

		/// <summary>
		/// Returns a bool configuration paramter from the xml config file
		/// </summary>
		/// <param name="doc">the config file</param>
		/// <param name="xpath">how to get to the node</param>
		/// <param name="bDefault">the default value if the node doesn't exist or is invalie</param>
		/// <returns>the bool value of the param, or bDefault if something goes wrong</returns>
		private bool GetConfigParamBool( XmlDocument doc, String xpath, bool bDefault )
		{
			String temp = GetConfigParamString( doc, xpath, null );
			try
			{
				return bool.Parse(temp);
			}
			catch( Exception )
			{
				return bDefault;
			}
		}

		/// <summary>
		/// Returns a UInt16 configuration parameter from the xml config file
		/// </summary>
		/// <param name="doc">the config file</param>
		/// <param name="xpath">the xpath to get to the node</param>
		/// <param name="nDefault">the default value if the node doesn't exist or is invalid</param>
		/// <returns>the UInt16 value of the param, or nDefault if something goes wrong</returns>
		private UInt16 GetConfigParamUInt16( XmlDocument doc, String xpath, UInt16 nDefault )
		{
			String temp = GetConfigParamString( doc, xpath, null );
			try
			{
				return UInt16.Parse(temp);
			}
			catch( Exception )
			{
				return nDefault;
			}
		}



		/// <summary>
		/// Return a Point configuration parameter from the xml config file
		/// </summary>
		/// <param name="doc">the config file</param>
		/// <param name="xpath">the xpath to get the node</param>
		/// <param name="pDefault">the default value if the node doesn't exist or is invalid</param>
		/// <returns>the Point value of the param or pDefault if something goes wrong</returns>
		private Point GetConfigParamPoint( XmlDocument doc, String xpath, Point pDefault )
		{
			String temp = GetConfigParamString( doc, xpath, null );
			try
			{
				String[] xy = temp.Split( new char[] { ',' } );
				return new Point( Int32.Parse( xy[0] ), Int32.Parse( xy[1] ) );
			}
			catch( Exception )
			{
				return pDefault;
			}
		}


		private String GetConfigParamPassword( XmlDocument doc, String xpath, String strDefault )
		{
			String temp = GetConfigParamString( doc, xpath, null );
			try
			{
				//decrypt the buffer
				return DPAPI.Decrypt( temp );
			}
			catch(Exception)
			{
				return strDefault;
			}
		}


		/// <summary>
		/// Create a new ToastConfig object with the given params
		/// </summary>
		/// <param name="server">The server</param>
		/// <param name="useSecure">Use a secure connection</param>
		/// <param name="account">The account to monitor</param>
		/// <param name="password">The accounts password</param>
		/// <param name="pollInterval">The polling interval - must be > 0</param>
		/// <param name="clickURLPathFmt">The format specifier of the path portion of the url to be opened when an item is clicked</param>
		/// <param name="location">The default location of the toaster window</param>
		/// <param name="soundFile">Sound to play when new msgs arrive</param>
		public ToastConfig( 
			String	server, 
			bool	useSecure, 
			String	account, 
			String	password, 
			UInt16	pollInterval, 
			String	clickURLPathFmt, 
			Point	location ,
			String	soundFile )
		{
			this.server = server;
			this.useSecure = useSecure;
			this.account = account;
			this.password = password;
			if( pollInterval > 0 )
			{
				this.pollInterval = pollInterval;
			}
			else 
			{
				this.pollInterval = DEFAULT_POLL_INTERVAL;
			}
			this.clickURLPathFmt = clickURLPathFmt;
			this.location = location;
			this.soundFile = soundFile;
		}


		/// <summary>
		/// Retrieve the param file name.
		/// </summary>
		/// <returns></returns>
		private static String ParamFilename()
		{
			String path = System.Windows.Forms.Application.UserAppDataPath;
			int idx = path.IndexOf( System.Windows.Forms.Application.ProductVersion );
			path = path.Substring( 0, idx ) + filename;
			return path;
		}

		/// <summary>
		/// Serialize the parameters to the param file
		/// </summary>
		public void Save()
		{
			XmlDocument doc = new XmlDocument();
			XmlElement zimbraToast = doc.CreateElement( CFG_ZIMBRA_TOAST );
			doc.AppendChild( zimbraToast );

			SaveElement( doc, zimbraToast, CFG_SERVER, server );
			SaveElement( doc, zimbraToast, CFG_USE_SECURE, useSecure.ToString() );
			SaveElement( doc, zimbraToast, CFG_ACCOUNT, account );
			SaveElement( doc, zimbraToast, CFG_PASSWORD, EncryptedPassword );
			SaveElement( doc, zimbraToast, CFG_POLL_INTERVAL, pollInterval.ToString() );
			SaveElement( doc, zimbraToast, CFG_CLICK_URL, clickURLPathFmt );
			
			String point = location.X.ToString() + "," + location.Y.ToString();
			SaveElement( doc, zimbraToast, CFG_LOCATION, point );
			SaveElement( doc, zimbraToast, CFG_SOUND_FILE, soundFile );

			doc.Save(ParamFilename());
		}

		
		/// <summary>
		/// Write the param to the xml file
		/// </summary>
		/// <param name="doc">the xml config document</param>
		/// <param name="parent">the parent xml node</param>
		/// <param name="paramName">the name of the param</param>
		/// <param name="paramValue">the param as a string</param>
		private static void SaveElement( XmlDocument doc, XmlElement parent, String paramName, String paramValue )
		{
			if( paramValue != null )
			{
				XmlElement e = doc.CreateElement( paramName );
				parent.AppendChild( e );
				e.InnerText = paramValue;
			}
		}


		/// <summary>
		/// The server to connecto to.  Can end in :{port}
		/// otherwise the defualt ports are used (80/443)
		/// </summary>
		public String Server 
		{
			get
			{ 
				try 
				{
					return server.Split( new char[] { ':' } )[0];
				}
				catch(Exception)
				{
					return server;
				}
			}
		}


		/// <summary>
		/// The port to connect to
		/// </summary>
		public UInt16 Port
		{
			get
			{
				UInt16 port = 80;
				if( useSecure ) 
				{
					port = 443;
				}

				try 
				{
					return UInt16.Parse( server.Split( new char[] { ':' } )[1] );
				} 
				catch(Exception)
				{
					return port;
				}
			}
		}

		/// <summary>
		/// The zimbra account name (aka email address)
		/// </summary>
		public String Account 
		{
			get{ return account; }
		}

		/// <summary>
		/// zimbra account password
		/// </summary>
		public String Password
		{
			get{ return password; }
		}

		/// <summary>
		/// use a secure connection to the server?
		/// </summary>
		public bool UseSecure
		{
			get{ return useSecure; }
		}



		/// <summary>
		/// how often to poll the server for updates
		/// </summary>
		public UInt16 PollInterval
		{
			get{ return pollInterval; }
		}


		/// <summary>
		/// format specifier of the path portion of the URL to open 
		/// when an item is clicked in the toaster
		/// </summary>
		public String ClickURLPathFmt
		{
			get{ return clickURLPathFmt; }
		}


		/// <summary>
		/// the default location of the toaster window
		/// </summary>
		public Point Location
		{
			get{ return location; }
			set{ location = value; }
		}



		/// <summary>
		/// Based on the current configration, get the URI for an item
		/// </summary>
		/// <param name="itemId"></param>
		/// <returns></returns>
		public String GetItemUri( String itemId )
		{
			System.Text.StringBuilder sb = new System.Text.StringBuilder( GetServerUri() );
			
			//append the configurable path portion
			sb.AppendFormat( this.clickURLPathFmt, itemId );

			return sb.ToString();
		}



		/// <summary>
		/// url to open a compose window in ZWC
		/// </summary>
		/// <param name="mailToUrl"></param>
		/// <returns></returns>
		public String GetMailtoUri( String mailToUrl )
		{
			System.Text.StringBuilder sb = new System.Text.StringBuilder( GetServerUri() );

			mailToUrl = mailToUrl.Replace( "mailto:", "to=" );
			int idx = mailToUrl.IndexOf( '?' );
			if( idx != -1 ) 
			{
				mailToUrl = mailToUrl.Substring( 0, idx ) + "&" + mailToUrl.Substring( idx + 1 );
			}

			sb.AppendFormat( "/zimbra/mail?view=compose&{0}", mailToUrl );

			return sb.ToString();
		}


		/// <summary>
		/// returns the server uri
		/// </summary>
		/// <returns></returns>
		public String GetServerUri()
		{
			bool bExcludePort = 
				(Port == 80  && !UseSecure) ||
				(Port == 443 && UseSecure );

			System.Text.StringBuilder sb = new System.Text.StringBuilder();

			//protocol/server part
			sb.AppendFormat( "http{0}://{1}{2}{3}",
				(UseSecure)?"s":"",
				Server,
				(bExcludePort)?"":":",
				(bExcludePort)?"":Port.ToString() );

			return sb.ToString();
		}


		/// <summary>
		/// encrypted password base64 encoded as a string
		/// </summary>
		public String EncryptedPassword
		{
			get
			{
				return DPAPI.Encrypt( DPAPI.KeyType.UserKey, this.password, null, PASSWORD_KEY );
			}
		}


		/// <summary>
		/// The sound file to play when new messages arrive
		/// </summary>
		public String SoundFile
		{
			get{ return soundFile; }
		}


	}
}
