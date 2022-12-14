package by.fxg.speceditor.scenes.screen;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.project.ProjectManager;
import by.fxg.speceditor.project.ProjectSolver;
import by.fxg.speceditor.scenes.ScenesProject;
import by.fxg.speceditor.std.ui.ISTDInputFieldListener;
import by.fxg.speceditor.std.ui.STDInputField;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.ColoredInputField;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UCheckbox;
import by.fxg.speceditor.utils.BaseSubscreen;
import by.fxg.speceditor.utils.SpecFileChooser;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ScenesSubscreenCreateProject extends BaseSubscreen implements ISTDInputFieldListener {
	private final ProjectSolver projectSolver;
	private ColoredInputField projectName, backupInterval;
	private UCheckbox enableGLTFViewport, enableBackups;
	private UButton buttonSelectFolder, buttonCreateProject;
	
	private boolean isProjectFolderValid, isProjectNameValid, isBackupIntervalValid;
	private FileHandle projectFolder;
	
	public ScenesSubscreenCreateProject(ProjectSolver projectSolver) {
		this.projectSolver = projectSolver;
		this.projectName = (ColoredInputField)new ColoredInputField().setBackgroundColor(UColor.redgray).setMaxLength(128).setListener(this, "projectName");
		this.backupInterval = (ColoredInputField)new ColoredInputField().setBackgroundColor(UColor.redgray).setMaxLength(8).setListener(this, "backupInterval");
		this.projectName.setNextField(this.backupInterval).setPreviousField(this.backupInterval);
		this.backupInterval.setNextField(this.projectName).setPreviousField(this.projectName);
		this.enableGLTFViewport = new UCheckbox(false).setEnabled(false);
		this.enableBackups = new UCheckbox(false);
		this.buttonSelectFolder = new UButton("Select project folder");
		this.buttonCreateProject = new UButton("Create project");
	}
	
	public void update(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		this.projectName.update();
		this.enableGLTFViewport.update();
		this.enableBackups.update();
		if (this.enableBackups.getValue()) this.backupInterval.update();
		
		if (this.buttonSelectFolder.isPressed()) {
			this.projectFolder = SpecFileChooser.get().openSingle(false, true);
			this.isProjectFolderValid = this.projectFolder != null && this.projectFolder.exists() && !this.projectFolder.child("project.ini").exists();
		}
		
		this.buttonCreateProject.setEnabled(this.isProjectFolderValid && this.isProjectNameValid && (this.enableBackups.getValue() ? this.isBackupIntervalValid : true));
		if (this.buttonCreateProject.isPressed()) {
			long backupInterval = this.enableBackups.getValue() && this.isBackupIntervalValid ? Utils.parseTime(this.backupInterval.getText()) : 600L; 
			ScenesProject project = new ScenesProject(this.projectSolver, this.projectFolder, this.projectName.getText(), this.enableBackups.getValue(), backupInterval);
			project.useLegacyRenderer = !this.enableGLTFViewport.getValue();
			project.saveConfiguration();
			ProjectManager.INSTANCE.setRecentProject(this.projectFolder);
			if (project.loadProject()) {
				ProjectManager.setCurrentProject(project);
				project.onProjectOpened();
			}
		}
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		foster.setString("Standard Scene project").draw(x + 5, y + height - 12, Align.left);
		foster.setString(Utils.format("Project path: ", this.projectFolder != null ? this.projectFolder.path() : "Not selected")).draw(x + 5, y + height - 30, Align.left);
		foster.setString("Project name:").draw(x + 5, y + height - 70, Align.left);
		foster.setString("Enable GLTF Viewport:").draw(x + 5, y + height - 90, Align.left);
		foster.setString("Enable backups:").draw(x + 5, y + height - 110, Align.left);
		
		this.buttonSelectFolder.render(shape, foster);
		this.projectName.setFoster(foster).render(batch, shape);
		this.enableGLTFViewport.render(shape);
		this.enableBackups.render(shape);
		if (this.enableBackups.getValue()) {
			foster.setString("Backup interval:").draw(x + 5, y + height - 129, Align.left);
			this.backupInterval.setFoster(foster).render(batch, shape);
		} else foster.setString("Backup interval: auto-saving disabled").draw(x + 5, y + height - 129, Align.left);
		this.buttonCreateProject.render(shape, foster);
	}
	
	public void onInputFieldTextChanged(STDInputField inputField, String id, String textAdded) {
		switch (id) {
			case "projectName": {
				this.isProjectNameValid = inputField.getText().length() > 0; 
				((ColoredInputField)inputField).setBackgroundColor(this.isProjectNameValid ? null : UColor.redgray);
			} break;
			case "backupInterval": {
				this.isBackupIntervalValid = Utils.parseTime(inputField.getText()) > 0;
				((ColoredInputField)inputField).setBackgroundColor(this.isBackupIntervalValid ? null : UColor.redgray);
			} break;
		}
	}

	public void resize(int subX, int subY, int subWidth, int subHeight) {
		this.projectName.setTransforms(subX + 10 + (int)SpecEditor.fosterNoDraw.setString("Project name:").getWidth(), subY + subHeight - 74, 150, 14);
		this.enableGLTFViewport.setTransforms(subX + 10 + (int)SpecEditor.fosterNoDraw.setString("Enable GLTF Viewport:").getWidth(), subY + subHeight - 93, 12, 12);
		this.enableBackups.setTransforms(subX + 10 + (int)SpecEditor.fosterNoDraw.setString("Enable backups:").getWidth(), subY + subHeight - 113, 12, 12);
		this.backupInterval.setTransforms(subX + 10 + (int)SpecEditor.fosterNoDraw.setString("Backup interval:").getWidth(), subY + subHeight - 132, 132, 14);
		this.buttonSelectFolder.setTransforms(subX + 5, subY + subHeight - 50, 150, 15);
		this.buttonCreateProject.setTransforms(subX + 5, subY + subHeight - 156, 230, 14);
	}
}
