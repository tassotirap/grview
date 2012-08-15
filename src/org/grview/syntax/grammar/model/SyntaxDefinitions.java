package org.grview.syntax.grammar.model;

public interface SyntaxDefinitions
{

	static class Helper
	{
		public static String getString(String key)
		{
			return key;
			/*
			 * IPluginDescriptor desc =
			 * Platform.getPluginRegistry().getPluginDescriptor
			 * ("org.eclipse.gef.asin"); //$NON-NLS-1$ try { return
			 * desc.getResourceString(key); } catch (MissingResourceException e)
			 * { return key; }
			 */
		}
	}

	public String EmptyNodeLabel = "L.A.";

	public String PasteAction_ActionToolTipText = Helper.getString("%PasteAction.ActionToolTipText_UI_"); //$NON-NLS-1$
	public String PasteAction_ActionLabelText = Helper.getString("%PasteAction.ActionLabelText_UI_"); //$NON-NLS-1$
	public String PasteAction_ActionDeleteCommandName = Helper.getString("%PasteAction.ActionDeleteCommandName_UI_");//$NON-NLS-1$

	public String CopyAction_ActionToolTipText = Helper.getString("%CopyAction.ActionToolTipText_UI_"); //$NON-NLS-1$
	public String CopyAction_ActionLabelText = Helper.getString("%CopyAction.ActionLabelText_UI_"); //$NON-NLS-1$
	public String CopyAction_ActionDeleteCommandName = Helper.getString("%CopyAction.ActionDeleteCommandName_UI_");//$NON-NLS-1$

	public String ZoomAction_ZoomIn_ActionToolTipText = Helper.getString("%ZoomAction.ZoomIn.ActionToolTipText_UI_");//$NON-NLS-1$
	public String ZoomAction_ZoomIn_ActionLabelText = Helper.getString("%ZoomAction.ZoomIn.ActionLabelText_UI_"); //$NON-NLS-1$

	public String ZoomAction_ZoomOut_ActionToolTipText = Helper.getString("%ZoomAction.ZoomOut.ActionToolTipText_UI_");//$NON-NLS-1$
	public String ZoomAction_ZoomOut_ActionLabelText = Helper.getString("%ZoomAction.ZoomOut.ActionLabelText_UI_"); //$NON-NLS-1$

	public String CreateAsinPage1_Title = Helper.getString("%CreateAsinPage1.Title"); //$NON-NLS-1$
	public String CreateAsinPage1_Description = Helper.getString("%CreateAsinPage1.Description"); //$NON-NLS-1$
	public String CreateAsinPage1_ModelNames_GroupName = Helper.getString("%CreateAsinPage1.ModelNames.GroupName"); //$NON-NLS-1$
	public String CreateAsinPage1_ModelNames_EmptyModelName = Helper.getString("%CreateAsinPage1.ModelNames.EmptyModelName"); //$NON-NLS-1$
	public String CreateAsinPage1_ModelNames_FourBitAdderModelName = Helper.getString("%CreateAsinPage1.ModelNames.FourBitAdderModelName"); //$NON-NLS-1$

	public String IncrementDecrementAction_Increment_ActionLabelText = Helper.getString("%IncrementDecrementAction.Increment.ActionLabelText"); //$NON-NLS-1$
	public String IncrementDecrementAction_Increment_ActionToolTipText = Helper.getString("%IncrementDecrementAction.Increment.ActionToolTipText"); //$NON-NLS-1$
	public String IncrementDecrementAction_Decrement_ActionLabelText = Helper.getString("%IncrementDecrementAction.Decrement.ActionLabelText"); //$NON-NLS-1$
	public String IncrementDecrementAction_Decrement_ActionToolTipText = Helper.getString("%IncrementDecrementAction.Decrement.ActionToolTipText"); //$NON-NLS-1$

	public String AlignmentAction_AlignSubmenu_ActionLabelText = Helper.getString("%AlignmentAction.AlignSubmenu.ActionLabelText"); //$NON-NLS-1$

	public String AsinPlugin_Category_ComplexParts_Label = Helper.getString("%AsinPlugin.Category.ComplexParts.Label"); //$NON-NLS-1$
	public String AsinPlugin_Category_Components_Label = Helper.getString("%AsinPlugin.Category.Components.Label"); //$NON-NLS-1$
	public String AsinPlugin_Category_ControlGroup_Label = Helper.getString("%AsinPlugin.Category.ControlGroup.Label"); //$NON-NLS-1$

	public String AsinPlugin_Tool_CreationTool_HalfAdder_Label = Helper.getString("%AsinPlugin.Tool.CreationTool.HalfAdder.Label"); //$NON-NLS-1$
	public String AsinPlugin_Tool_CreationTool_HalfAdder_Description = Helper.getString("%AsinPlugin.Tool.CreationTool.HalfAdder.Description"); //$NON-NLS-1$
	public String AsinPlugin_Tool_CreationTool_FullAdder_Label = Helper.getString("%AsinPlugin.Tool.CreationTool.FullAdder.Label"); //$NON-NLS-1$
	public String AsinPlugin_Tool_CreationTool_FullAdder_Description = Helper.getString("%AsinPlugin.Tool.CreationTool.FullAdder.Description"); //$NON-NLS-1$
	public String AsinPlugin_Tool_CreationTool_AsinLabel = Helper.getString("%AsinPlugin.Tool.CreationTool.AsinLabel"); //$NON-NLS-1$
	public String AsinPlugin_Tool_CreationTool_Label_Label = Helper.getString("%AsinPlugin.Tool.CreationTool.Label.Label"); //$NON-NLS-1$
	public String AsinPlugin_Tool_CreationTool_Label_Description = Helper.getString("%AsinPlugin.Tool.CreationTool.Label.Description"); //$NON-NLS-1$
	public String AsinPlugin_Tool_CreationTool_FlowContainer_Label = Helper.getString("%AsinPlugin.Tool.CreationTool.FlowContainer.Label"); //$NON-NLS-1$
	public String AsinPlugin_Tool_CreationTool_FlowContainer_Description = Helper.getString("%AsinPlugin.Tool.CreationTool.FlowContainer.Description"); //$NON-NLS-1$
	public String AsinPlugin_Tool_CreationTool_LED_Label = Helper.getString("%AsinPlugin.Tool.CreationTool.LED.Label"); //$NON-NLS-1$
	public String AsinPlugin_Tool_CreationTool_LED_Description = Helper.getString("%AsinPlugin.Tool.CreationTool.LED.Description"); //$NON-NLS-1$

	public String AsinPlugin_Tool_CreationTool_NTerminal_Label = Helper.getString("%AsinPlugin.Tool.CreationTool.NTerminal.Label"); //$NON-NLS-1$
	public String AsinPlugin_Tool_CreationTool_NTerminal_Description = Helper.getString("%AsinPlugin.Tool.CreationTool.NTerminal.Description"); //$NON-NLS-1$
	public String AsinPlugin_Tool_CreationTool_Terminal_Label = Helper.getString("%AsinPlugin.Tool.CreationTool.Terminal.Label"); //$NON-NLS-1$
	public String AsinPlugin_Tool_CreationTool_Terminal_Description = Helper.getString("%AsinPlugin.Tool.CreationTool.Terminal.Description"); //$NON-NLS-1$
	public String AsinPlugin_Tool_CreationTool_LeftSide_Label = Helper.getString("%AsinPlugin.Tool.CreationTool.LeftSide.Label"); //$NON-NLS-1$
	public String AsinPlugin_Tool_CreationTool_LeftSide_Description = Helper.getString("%AsinPlugin.Tool.CreationTool.LeftSide.Description"); //$NON-NLS-1$
	public String AsinPlugin_Tool_CreationTool_LambdaAlternative_Label = Helper.getString("%AsinPlugin.Tool.CreationTool.LambdaAlternative.Label"); //$NON-NLS-1$
	public String AsinPlugin_Tool_CreationTool_LambdaAlternative_Description = Helper.getString("%AsinPlugin.Tool.CreationTool.LambdaAlternative.Description"); //$NON-NLS-1$

	public String AsinPlugin_Tool_CreationTool_XORGate_Label = Helper.getString("%AsinPlugin.Tool.CreationTool.XORGate.Label"); //$NON-NLS-1$
	public String AsinPlugin_Tool_CreationTool_XORGate_Description = Helper.getString("%AsinPlugin.Tool.CreationTool.XORGate.Description"); //$NON-NLS-1$
	public String AsinPlugin_Tool_CreationTool_ANDGate_Label = Helper.getString("%AsinPlugin.Tool.CreationTool.ANDGate.Label"); //$NON-NLS-1$
	public String AsinPlugin_Tool_CreationTool_ANDGate_Description = Helper.getString("%AsinPlugin.Tool.CreationTool.ANDGate.Description"); //$NON-NLS-1$
	public String AsinPlugin_Tool_CreationTool_LiveOutput_Label = Helper.getString("%AsinPlugin.Tool.CreationTool.LiveOutput.Label"); //$NON-NLS-1$
	public String AsinPlugin_Tool_CreationTool_LiveOutput_Description = Helper.getString("%AsinPlugin.Tool.CreationTool.LiveOutput.Description"); //$NON-NLS-1$
	public String AsinPlugin_Tool_CreationTool_Ground_Label = Helper.getString("%AsinPlugin.Tool.CreationTool.Ground.Label"); //$NON-NLS-1$
	public String AsinPlugin_Tool_CreationTool_Ground_Description = Helper.getString("%AsinPlugin.Tool.CreationTool.Ground.Description"); //$NON-NLS-1$
	public String AsinPlugin_Tool_SelectionTool_SelectionTool_Label = Helper.getString("%AsinPlugin.Tool.SelectionTool.SelectionTool.Label"); //$NON-NLS-1$
	public String AsinPlugin_Tool_SelectionTool_SelectionTool_Description = Helper.getString("%AsinPlugin.Tool.SelectionTool.SelectionTool.Description"); //$NON-NLS-1$
	public String AsinPlugin_Tool_MarqueeSelectionTool_MarqueeSelectionTool_Label = Helper.getString("%AsinPlugin.Tool.MarqueeSelectionTool.MarqueeSelectionTool.Label"); //$NON-NLS-1$
	public String AsinPlugin_Tool_MarqueeSelectionTool_MarqueeSelectionTool_Description = Helper.getString("%AsinPlugin.Tool.MarqueeSelectionTool.MarqueeSelectionTool.Description"); //$NON-NLS-1$
	public String AsinPlugin_Tool_ConnectionCreationTool_Sucessor_Label = Helper.getString("%AsinPlugin.Tool.ConnectionCreationTool.Sucessor.Label"); //$NON-NLS-1$
	public String AsinPlugin_Tool_ConnectionCreationTool_Sucessor_Description = Helper.getString("%AsinPlugin.Tool.ConnectionCreationTool.Sucessor.Description"); //$NON-NLS-1$
	public String AsinPlugin_Tool_ConnectionCreationTool_Alternative_Label = Helper.getString("%AsinPlugin.Tool.ConnectionCreationTool.Alternative.Label"); //$NON-NLS-1$
	public String AsinPlugin_Tool_ConnectionCreationTool_Alternative_Description = Helper.getString("%AsinPlugin.Tool.ConnectionCreationTool.Alternative.Description"); //$NON-NLS-1$

	public String IncrementDecrementCommand_LabelText = Helper.getString("%IncrementDecrementCommand.LabelText"); //$NON-NLS-1$
	public String AsinContainerEditPolicy_OrphanCommandLabelText = Helper.getString("%AsinContainerEditPolicy.OrphanCommandLabelText"); //$NON-NLS-1$
	public String AsinElementEditPolicy_OrphanCommandLabelText = Helper.getString("%AsinElementEditPolicy.OrphanCommandLabelText"); //$NON-NLS-1$
	public String AsinXYLayoutEditPolicy_AddCommandLabelText = Helper.getString("%AsinXYLayoutEditPolicy.AddCommandLabelText"); //$NON-NLS-1$
	public String AsinXYLayoutEditPolicy_CreateCommandLabelText = Helper.getString("%AsinXYLayoutEditPolicy.CreateCommandLabelText"); //$NON-NLS-1$
	public String AddCommand = Helper.getString("%AddCommand.Label"); //$NON-NLS-1$
	public String AddCommand_Description = Helper.getString("Add"); //$NON-NLS-1$
	public String AddRoutineCommand = Helper.getString("AddRoutine");
	public String AddRoutineCommand_Description = Helper.getString("Add Semantic Routine");
	public String RemoveRoutineCommand = Helper.getString("RemoveRoutine");
	public String RemoveRoutineCommand_Description = Helper.getString("Remove Semantic Routine");
	public String AndGate_LabelText = Helper.getString("%EndNode.LabelText"); //$NON-NLS-1$

	public String NTerminal = Helper.getString("Non Terminal"); //$NON-NLS-1$
	public String Terminal = Helper.getString("Terminal"); //$NON-NLS-1$
	public String LeftSide = Helper.getString("LeftSide"); //$NON-NLS-1$
	public String LambdaAlternative = Helper.getString("Lambda Alt."); //$NON-NLS-1$
	public String Label = Helper.getString("Label");
	public String Start = Helper.getString("Start");
	public String SucConnection = Helper.getString("Sucessor");
	public String AltConnection = Helper.getString("Alternative");
	public String Node = Helper.getString("Node");
	public String Set = Helper.getString("Set");

	public String GrammarGenerateCommand_Text = Helper.getString("%GrammarGenerate.Text");//$NON-NLS-1$
	public String ParsingAction_Text = Helper.getString("%ParsingAction.Text"); //$NON-NLS-1$

	public String GraphicalEditor_FILE_DELETED_TITLE_UI = Helper.getString("%GraphicalEditor.FILE_DELETED_TITLE_UI_"); //$NON-NLS-1$
	public String GraphicalEditor_FILE_DELETED_WITHOUT_SAVE_INFO = Helper.getString("%GraphicalEditor.FILE_DELETED_WITHOUT_SAVE_INFO_");//$NON-NLS-1$
	public String GraphicalEditor_SAVE_BUTTON_UI = Helper.getString("%GraphicalEditor.SAVE_BUTTON_UI_"); //$NON-NLS-1$
	public String GraphicalEditor_CLOSE_BUTTON_UI = Helper.getString("%GraphicalEditor.CLOSE_BUTTON_UI_"); //$NON-NLS-1$

	public String MultiDelete = Helper.getString("%MultiDelete");
	public String SingleDelete = Helper.getString("%SingleDelete");

	public String MoveCommand = Helper.getString("%MoveCommand");
	public String MoveCommand_Description = Helper.getString("Move");
	public String DisconnectionCommand = Helper.getString("%DisconnectionCommand");
	public String DisconnectionCommand_Description = Helper.getString("Disconnection");
	public String ConnectionCommand = Helper.getString("%ConnectionCommand.Label"); //$NON-NLS-1$
	public String ConnectionCommand_Description = Helper.getString("Connection"); //$NON-NLS-1$
	public String RenameCommand = Helper.getString("%RenameCommand");
	public String RenameCommand_Description = Helper.getString("Rename");
	public String CreateCommand_Label = Helper.getString("%CreateCommand.Label"); //$NON-NLS-1$
	public String CreateCommand_Description = Helper.getString("Create"); //$NON-NLS-1$
	public String DeleteCommand = Helper.getString("%DeleteCommand.Label"); //$NON-NLS-1$
	public String DeleteCommand_Description = Helper.getString("Delete"); //$NON-NLS-1$
	public String DimensionPropertySource_Property_Width_Label = Helper.getString("%DimensionPropertySource.Property.Width.Label"); //$NON-NLS-1$
	public String DimensionPropertySource_Property_Height_Label = Helper.getString("%DimensionPropertySource.Property.Height.Label"); //$NON-NLS-1$
	public String GroundOutput_LabelText = Helper.getString("%GroundOutput.LabelText"); //$NON-NLS-1$
	public String PropertyDescriptor_LED_Value = Helper.getString("%PropertyDescriptor.LED.Value"); //$NON-NLS-1$
	public String LED_LabelText = Helper.getString("%LED.LabelText"); //$NON-NLS-1$
	public String LiveOutput_LabelText = Helper.getString("%LiveOutput.LabelText"); //$NON-NLS-1$
	public String LocationPropertySource_Property_X_Label = Helper.getString("%LocationPropertySource.Property.X.Label"); //$NON-NLS-1$
	public String LocationPropertySource_Property_Y_Label = Helper.getString("%LocationPropertySource.Property.Y.Label"); //$NON-NLS-1$
	public String PropertyDescriptor_AsinDiagram_ConnectionRouter = Helper.getString("%PropertyDescriptor.AsinDiagram.ConnectionRouter"); //$NON-NLS-1$
	public String PropertyDescriptor_AsinDiagram_Manual = Helper.getString("%PropertyDescriptor.AsinDiagram.Manual"); //$NON-NLS-1$
	public String PropertyDescriptor_AsinDiagram_Manhattan = Helper.getString("%PropertyDescriptor.AsinDiagram.Manhattan"); //$NON-NLS-1$
	public String AsinDiagram_LabelText = Helper.getString("%AsinDiagram.LabelText"); //$NON-NLS-1$
	public String PropertyDescriptor_Label_Text = Helper.getString("%PropertyDescriptor.Label.Text"); //$NON-NLS-1$
	public String PropertyDescriptor_AsinSubPart_Size = Helper.getString("%PropertyDescriptor.AsinSubPart.Size"); //$NON-NLS-1$
	public String PropertyDescriptor_AsinSubPart_Location = Helper.getString("%PropertyDescriptor.AsinSubPart.Location"); //$NON-NLS-1$
	public String OrGate_LabelText = Helper.getString("%LambdaAlternative.LabelText"); //$NON-NLS-1$
	public String OrphanChildCommand_Label = Helper.getString("%OrphanChildCommand.Label"); //$NON-NLS-1$
	public String ReorderPartCommand_Label = Helper.getString("%ReorderPartCommand.Label"); //$NON-NLS-1$
	public String ReorderPartCommand_Description = Helper.getString("%ReorderPartCommand.Description"); //$NON-NLS-1$
	public String SetLocationCommand_Description = Helper.getString("%SetLocationCommand.Description"); //$NON-NLS-1$
	public String SetLocationCommand_Label_Location = Helper.getString("%SetLocationCommand.Label.Location"); //$NON-NLS-1$
	public String SetLocationCommand_Label_Resize = Helper.getString("%SetLocationCommand.Label.Resize"); //$NON-NLS-1$

	public String ViewMenu_LabelText = Helper.getString("%ViewMenu.LabelText_UI_"); //$NON-NLS-1$

	public String PaletteCustomizer_InvalidCharMessage = Helper.getString("%PaletteCustomizer.InvalidCharMessage"); //$NON-NLS-1$

	public String XORGate_LabelText = Helper.getString("%XORGate.LabelText"); //$NON-NLS-1$
	public String Wire_LabelText = Helper.getString("%Wire.LabelText"); //$NON-NLS-1$

	public String SemanticLabel = "Semantic Label";
}