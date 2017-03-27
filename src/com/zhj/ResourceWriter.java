package com.zhj;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

/**
 * Created by Fred Zhao on 2017/3/27.
 */
public class ResourceWriter extends WriteCommandAction.Simple {


    protected ResourceWriter(Project project, String commandName) {
        super(project, commandName);
    }

    @Override
    protected void run() throws Throwable {

    }
}
