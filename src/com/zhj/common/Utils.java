package com.zhj.common;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.ui.awt.RelativePoint;


import java.util.regex.Pattern;

/**
 * Created by Fred Zhao on 2017/3/27.
 */
public class Utils {
    private static final Logger log = Logger.getInstance(Utils.class);

    private static final String FILE_TYPE_XML = "XML";
    private static final String FILE_TYPE_JAVA = "JAVA";
    private static final String LAYOUT_DIRECTORY = "layout";

    public static Sdk findAndroidSDK() {
        Sdk[] allJDKs = ProjectJdkTable.getInstance().getAllJdks();
        for (Sdk sdk : allJDKs) {
            if (sdk.getSdkType().getName().toLowerCase().contains("android")) {
                return sdk;
            }
        }
        return null; // no Android SDK found
    }

    public static boolean isResource(Editor editor, PsiFile psiFile) {
        int offset = editor.getCaretModel().getOffset();
        PsiElement candidateA = psiFile.findElementAt(offset);
        PsiElement candidateB = psiFile.findElementAt(offset - 1);
        if (isAllowResource(candidateA.getText(), psiFile)) {
            return true;
        }
        return isAllowResource(candidateB.getText(), psiFile);
    }

    public static String getTargetStr(Editor editor, PsiFile psiFile) {
        int offset = editor.getCaretModel().getOffset();
        PsiElement candidateA = psiFile.findElementAt(offset);
        PsiElement candidateB = psiFile.findElementAt(offset - 1);
        String name = resetCandidateName(candidateA.getText());
        if (!isBlankStr(name)) {
            return name;
        }
        return resetCandidateName(candidateB.getText());
    }

    private static boolean isAllowResource(String candidateName, PsiFile psiFile) {
        String name = resetCandidateName(candidateName);
        return isFileAllow(psiFile) && (isColorCandidate(name) || (isDimenCandidate(name) && isLayoutXML(psiFile)) || isStringCandidate(name));
    }

    private static String resetCandidateName(String name) {
        if (isBlankStr(name)) return "";
        return (name.startsWith("\"") && name.endsWith("\"")) ? ((name.length() > 2) ? name.substring(1, name.length() - 1) : "") : name;
    }

    public static boolean isColorCandidate(String candidateName) {
        if (isBlankStr(candidateName)) return false;
        int length = candidateName.length();
        String pattern = "#[A-Fa-f0-9]+";
        return (length == 4 || length == 7 || length == 9) && Pattern.compile(pattern).matcher(candidateName).matches();
    }

    public static boolean isDimenCandidate(String candidateName) {
        if (isBlankStr(candidateName)) return false;
        String pattern = "[0-9]+(sp|dp|dip|px)";
        return null != candidateName && Pattern.compile(pattern).matcher(candidateName).matches();
    }

    public static boolean isStringCandidate(String candidateName) {
        if (isBlankStr(candidateName)) return false;
        String pattern = "[\u4e00-\u9fa5]";
        return Pattern.compile(pattern).matcher(candidateName).find();
    }

    private static boolean isFileAllow(PsiFile psiFile) {
        return isLayoutXML(psiFile) || FILE_TYPE_JAVA.equals(psiFile.getFileType().getName());
    }

    private static boolean isLayoutXML(PsiFile psiFile) {
        return psiFile.getParent().getName().startsWith(LAYOUT_DIRECTORY) && FILE_TYPE_XML.equals(psiFile.getFileType().getName());
    }

    public static boolean isBlankStr(String str) {
        return null == str || str.length() == 0;
    }

    /**
     * Display simple notification - information
     *
     * @param project
     * @param text
     */
    public static void showInfoNotification(Project project, String text) {
        showNotification(project, MessageType.INFO, text);
    }

    /**
     * Display simple notification - error
     *
     * @param project
     * @param text
     */
    public static void showErrorNotification(Project project, String text) {
        showNotification(project, MessageType.ERROR, text);
    }

    /**
     * Display simple notification of given type
     *
     * @param project
     * @param type
     * @param text
     */
    public static void showNotification(Project project, MessageType type, String text) {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);

        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(text, type, null)
                .setFadeoutTime(7500)
                .createBalloon()
                .show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);
    }
}
