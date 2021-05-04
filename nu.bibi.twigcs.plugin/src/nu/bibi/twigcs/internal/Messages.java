/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.internal;

import org.eclipse.osgi.util.NLS;

/**
 * Properties wrapper.
 */ 
public final class Messages extends NLS {
	
	/*
	 * the bundle name
	 */
	private static final String BUNDLE_NAME = "nu.bibi.twigcs.internal.messages"; //$NON-NLS-1$
	
	/*
	 * prevent instance creation
	 */
	private Messages() {
	}
	
	public static String FolderSelectionDialog_Error_Already_Selected;
	public static String FolderSelectionDialog_Error_No_Selection;
	public static String FolderSelectionDialog_Error_Not_A_Folder;
	public static String FolderSelectionDialog_Message;
	public static String FolderSelectionDialog_Title;
	public static String IOExecutor_Error_Interrupted;
	public static String Preferences_Error_Save;
	public static String PreferencesPage_Description;
	public static String PreferencesPage_Display;
	public static String PreferencesPage_Error_Path;
	public static String PreferencesPage_Path;
	public static String PreferencesPage_Reporter;
	public static String PreferencesPage_Severity;
	public static String PreferencesPage_Test;
	public static String PreferencesPage_Test_Error;
	public static String PreferencesPage_Test_Success;
	public static String PreferencesPage_Version;
	public static String ProjectPropertyPage_Add;
	public static String ProjectPropertyPage_Description;
	public static String ProjectPropertyPage_Edit;
	public static String ProjectPropertyPage_Exclude;
	public static String ProjectPropertyPage_Include;
	public static String ProjectPropertyPage_Override;
	public static String ProjectPropertyPage_Remove;
	public static String Resolution_Error_Charset;
	public static String Resolution_Error_Read;
	public static String Resolution_Error_Write;
	public static String Resolution_Lower_Case;
	public static String Resolution_No_Space;
	public static String Resolution_One_Space;
	public static String Resolution_Unused_Macro;
	public static String Resolution_Unused_Variable;
	public static String ResourceListener_Update;
	public static String ResourceText_Error_Index;
	public static String ResourceText_Error_Read;
	public static String ResourceText_Error_Content;
	public static String SeverityDeserializer_Error;
	public static String TwigcsBuilder_Process_Files;
	public static String TwigcsNatureHandler_Error_Execute;
	public static String TwigcsProcessor_Error_No_Path;
	public static String TwigcsProcessor_Error_Not_Exist;
	public static String TwigcsProcessor_Error_Paths_Empty;
	public static String TwigcsProcessor_Error_Real_Path;
	public static String TwigcsResultParser_Error;
	public static String ValidationVisitor_Error_Validate_Code;
	public static String ValidationVisitor_Error_Validate_Name;

	/*
	 * initialise messages
	 */
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}