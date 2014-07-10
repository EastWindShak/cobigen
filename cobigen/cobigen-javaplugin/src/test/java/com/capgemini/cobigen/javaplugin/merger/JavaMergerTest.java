/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.javaplugin.merger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.LinkedList;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import com.capgemini.cobigen.exceptions.MergeException;
import com.capgemini.cobigen.javaplugin.merger.JavaMerger;
import com.capgemini.cobigen.javaplugin.merger.libextension.ModifyableClassLibraryBuilder;
import com.capgemini.cobigen.javaplugin.merger.libextension.ModifyableJavaClass;
import com.google.common.io.Files;
import com.thoughtworks.qdox.library.ClassLibraryBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaConstructor;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.JavaType;

/**
 * TestCase testing {@link JavaMerger}
 * 
 * @author mbrunnli (04.04.2013)
 */
public class JavaMergerTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = "src/test/resources/JavaMergerTest/";

    /**
     * Test of {@link JavaMerger} merging imports
     * @throws Exception
     * @author mbrunnli (04.04.2013)
     */
    @Test
    public void testMergeImport_defaultNonOverride() throws Exception {
        File baseFile = new File(testFileRootPath + "BaseFile_import.java");
        File patchFile = new File(testFileRootPath + "PatchFile_import.java");
        JavaSource mergedSource = getMergedSource(baseFile, patchFile, false);

        Assert.assertEquals("com.capgemini", mergedSource.getPackageName());
        Assert.assertEquals(2, mergedSource.getImports().size());
        Assert.assertTrue(mergedSource.getImports().contains("com.capgemini.BaseClassImport"));
        Assert.assertTrue(mergedSource.getImports().contains("com.capgemini.PatchClassImport"));
    }

    /**
     * Test of {@link JavaMerger} merging fields
     * @throws Exception
     * @author mbrunnli (04.04.2013)
     */
    @Test
    public void testMergeProperty_defaultNonOverride() throws Exception {
        File baseFile = new File(testFileRootPath + "BaseFile_field.java");
        File patchFile = new File(testFileRootPath + "PatchFile_field.java");
        JavaSource mergedSource = getMergedSource(baseFile, patchFile, false);

        Assert.assertEquals("com.capgemini", mergedSource.getPackageName());
        Assert.assertEquals(0, mergedSource.getImports().size());
        Assert.assertEquals(1, mergedSource.getClasses().size());

        JavaClass clsFooBar = mergedSource.getClassByName("com.capgemini.FooBar");
        Assert.assertNotNull(clsFooBar);
        Assert.assertEquals(3, clsFooBar.getFields().size());

        JavaField field = clsFooBar.getFieldByName("baseField");
        Assert.assertNotNull(field);
        Assert.assertEquals(true, field.getInitializationExpression().equals("0"));
    }

    /**
     * Test of {@link JavaMerger} merging methods
     * @throws Exception
     * @author mbrunnli (04.04.2013)
     */
    @Test
    public void testMergeMethod_defaultNonOverride() throws Exception {
        File baseFile = new File(testFileRootPath + "BaseFile_method.java");
        File patchFile = new File(testFileRootPath + "PatchFile_method.java");
        JavaSource mergedSource = getMergedSource(baseFile, patchFile, false);

        Assert.assertEquals("com.capgemini", mergedSource.getPackageName());
        Assert.assertEquals(0, mergedSource.getImports().size());
        Assert.assertEquals(1, mergedSource.getClasses().size());

        JavaClass clsFooBar = mergedSource.getClassByName("com.capgemini.FooBar");
        Assert.assertNotNull(clsFooBar);
        Assert.assertEquals(2, clsFooBar.getConstructors().size());
        Assert.assertEquals(2, clsFooBar.getMethods().size());

        JavaConstructor emptyConstructor = clsFooBar.getConstructor(new LinkedList<JavaType>());
        Assert.assertNotNull(emptyConstructor);
        Assert.assertEquals("", emptyConstructor.getSourceCode().trim());

        JavaMethod baseMethod = clsFooBar.getMethodBySignature("baseMethod", new LinkedList<JavaType>());
        Assert.assertNotNull(baseMethod);
        Assert.assertEquals(void.class.getCanonicalName(), baseMethod.getReturnType(true).getCanonicalName());
    }

    /**
     * Test of {@link JavaMerger} merging classes recursively
     * @throws Exception
     * @author mbrunnli (04.04.2013)
     */
    @Test
    public void testMergeInnerClasses_defaultNonOverride() throws Exception {
        File baseFile = new File(testFileRootPath + "BaseFile_innerClass.java");
        File patchFile = new File(testFileRootPath + "PatchFile_innerClass.java");
        JavaSource mergedSource = getMergedSource(baseFile, patchFile, false);

        Assert.assertEquals("com.capgemini", mergedSource.getPackageName());
        Assert.assertEquals(0, mergedSource.getImports().size());
        Assert.assertEquals(1, mergedSource.getClasses().size());

        JavaClass clsFooBar = mergedSource.getClassByName("com.capgemini.FooBar");
        Assert.assertNotNull(clsFooBar);
        Assert.assertEquals(3, clsFooBar.getNestedClasses().size());

        JavaClass innerClass = clsFooBar.getNestedClassByName("InnerBaseClass");
        Assert.assertNotNull(innerClass);
        Assert.assertEquals(2, innerClass.getMethods().size());
        Assert.assertEquals(2, innerClass.getFields().size());
        Assert.assertEquals(1, innerClass.getNestedClasses().size());

        JavaField innerField = innerClass.getFieldByName("innerBaseField");
        Assert.assertNotNull(innerField);
        Assert.assertEquals(true, innerField.getInitializationExpression().equals("0"));

        JavaMethod baseMethod =
            innerClass.getMethodBySignature("innerBaseMethod", new LinkedList<JavaType>());
        Assert.assertNotNull(baseMethod);
        Assert.assertEquals(void.class.getCanonicalName(), baseMethod.getReturnType(true).getCanonicalName());

        JavaClass mergedInnerEnum = innerClass.getNestedClassByName("InnerBaseEnum");
        Assert.assertNotNull(mergedInnerEnum);
        Assert.assertEquals(2, mergedInnerEnum.getFields().size());

        JavaClass mergedEnum = clsFooBar.getNestedClassByName("BaseEnum");
        Assert.assertNotNull(mergedEnum);
        Assert.assertEquals(2, mergedEnum.getFields().size());

    }

    /**
     * Test of {@link JavaMerger} merging imports
     * @throws Exception
     * @author mbrunnli (04.04.2013)
     */
    @Test
    public void testMergeImport_Override() throws Exception {
        File baseFile = new File(testFileRootPath + "BaseFile_import.java");
        File patchFile = new File(testFileRootPath + "PatchFile_import.java");
        JavaSource mergedSource = getMergedSource(baseFile, patchFile, true);

        Assert.assertEquals("com.capgemini", mergedSource.getPackageName());
        Assert.assertEquals(2, mergedSource.getImports().size());
        Assert.assertTrue(mergedSource.getImports().contains("com.capgemini.conflicting.BaseClassImport"));
        Assert.assertTrue(mergedSource.getImports().contains("com.capgemini.PatchClassImport"));
    }

    /**
     * Test of {@link JavaMerger} merging fields
     * @throws Exception
     * @author mbrunnli (04.04.2013)
     */
    @Test
    public void testMergeProperty_Override() throws Exception {
        File baseFile = new File(testFileRootPath + "BaseFile_field.java");
        File patchFile = new File(testFileRootPath + "PatchFile_field.java");
        JavaSource mergedSource = getMergedSource(baseFile, patchFile, true);

        Assert.assertEquals("com.capgemini", mergedSource.getPackageName());
        Assert.assertEquals(0, mergedSource.getImports().size());
        Assert.assertEquals(1, mergedSource.getClasses().size());

        JavaClass clsFooBar = mergedSource.getClassByName("com.capgemini.FooBar");
        Assert.assertNotNull(clsFooBar);
        Assert.assertEquals(3, clsFooBar.getFields().size());

        JavaField field = clsFooBar.getFieldByName("baseField");
        Assert.assertNotNull(field);
        Assert.assertEquals(true, field.getInitializationExpression().equals("1"));
    }

    /**
     * Test of {@link JavaMerger} merging methods
     * @throws Exception
     * @author mbrunnli (04.04.2013)
     */
    @Test
    public void testMergeMethod_Override() throws Exception {
        File baseFile = new File(testFileRootPath + "BaseFile_method.java");
        File patchFile = new File(testFileRootPath + "PatchFile_method.java");
        JavaSource mergedSource = getMergedSource(baseFile, patchFile, true);

        Assert.assertEquals("com.capgemini", mergedSource.getPackageName());
        Assert.assertEquals(0, mergedSource.getImports().size());
        Assert.assertEquals(1, mergedSource.getClasses().size());

        JavaClass clsFooBar = mergedSource.getClassByName("com.capgemini.FooBar");
        Assert.assertNotNull(clsFooBar);
        Assert.assertEquals(2, clsFooBar.getConstructors().size());
        Assert.assertEquals(2, clsFooBar.getMethods().size());

        JavaConstructor emptyConstructor = clsFooBar.getConstructor(new LinkedList<JavaType>());
        Assert.assertNotNull(emptyConstructor);
        Assert.assertEquals("super();", emptyConstructor.getSourceCode().trim());

        JavaMethod baseMethod = clsFooBar.getMethodBySignature("baseMethod", new LinkedList<JavaType>());
        Assert.assertNotNull(baseMethod);
        Assert.assertEquals(String.class.getCanonicalName(), baseMethod.getReturnType(true)
            .getCanonicalName());
    }

    /**
     * Test of {@link JavaMerger} merging classes recursively
     * @throws Exception
     * @author mbrunnli (04.04.2013)
     */
    @Test
    public void testMergeInnerClasses_Override() throws Exception {
        File baseFile = new File(testFileRootPath + "BaseFile_innerClass.java");
        File patchFile = new File(testFileRootPath + "PatchFile_innerClass.java");
        JavaSource mergedSource = getMergedSource(baseFile, patchFile, true);

        Assert.assertEquals("com.capgemini", mergedSource.getPackageName());
        Assert.assertEquals(0, mergedSource.getImports().size());
        Assert.assertEquals(1, mergedSource.getClasses().size());

        JavaClass clsFooBar = mergedSource.getClassByName("com.capgemini.FooBar");
        Assert.assertNotNull(clsFooBar);
        Assert.assertEquals(3, clsFooBar.getNestedClasses().size());

        JavaClass innerClass = clsFooBar.getNestedClassByName("InnerBaseClass");
        Assert.assertNotNull(innerClass);
        Assert.assertEquals(2, innerClass.getMethods().size());
        Assert.assertEquals(2, innerClass.getFields().size());
        Assert.assertEquals(1, innerClass.getNestedClasses().size());

        JavaField innerField = innerClass.getFieldByName("innerBaseField");
        Assert.assertNotNull(innerField);
        Assert.assertEquals(true, innerField.getInitializationExpression().equals("1"));

        JavaMethod baseMethod =
            innerClass.getMethodBySignature("innerBaseMethod", new LinkedList<JavaType>());
        Assert.assertNotNull(baseMethod);
        Assert.assertEquals(String.class.getCanonicalName(), baseMethod.getReturnType(true)
            .getCanonicalName());

        JavaClass mergedInnerEnum = innerClass.getNestedClassByName("InnerBaseEnum");
        Assert.assertNotNull(mergedInnerEnum);
        Assert.assertEquals(2, mergedInnerEnum.getFields().size());

        JavaClass mergedEnum = clsFooBar.getNestedClassByName("BaseEnum");
        Assert.assertNotNull(mergedEnum);
        Assert.assertEquals(2, mergedEnum.getFields().size());

    }

    /**
     * Tests whether the contents will be rewritten after parsing and printing with QDox with the right
     * encoding
     * @throws IOException
     * @throws MergeException
     * @author mbrunnli (12.04.2013)
     */
    @Test
    public void testReadingEncoding() throws IOException, MergeException {
        File baseFile = new File(testFileRootPath + "BaseFile_encoding_UTF-8.java");
        File patchFile = new File(testFileRootPath + "PatchFile_encoding.java");
        String mergedContents =
            new JavaMerger("", false).merge(baseFile, FileUtils.readFileToString(patchFile), "UTF-8");
        JavaSource mergedSource = getJavaClass(new StringReader(mergedContents)).getSource();
        Assert.assertTrue(mergedSource.toString().contains("enthält"));

        baseFile = new File(testFileRootPath + "BaseFile_encoding_ISO-8859-1.java");
        mergedContents =
            new JavaMerger("", false).merge(baseFile, FileUtils.readFileToString(patchFile), "ISO-8859-1");
        mergedSource = getJavaClass(new StringReader(mergedContents)).getSource();
        Assert.assertTrue(mergedSource.toString().contains("enthält"));
    }

    /**
     * Tests whether the output file does not contain different line endings
     * @throws IOException
     *             test fails
     * @throws MergeException
     * @author mbrunnli (04.06.2013)
     */
    @Test
    public void testConsistentLineEndings() throws IOException, MergeException {
        File baseFile = new File(testFileRootPath + "BaseFile_innerClass.java");
        File patchFile = new File(testFileRootPath + "PatchFile_innerClass.java");
        String mergedContents =
            new JavaMerger("", false).merge(baseFile, FileUtils.readFileToString(patchFile), "UTF-8");

        boolean eol1 = mergedContents.contains("\r\n");
        mergedContents = mergedContents.replaceAll("\r\n", "");
        boolean eol2 = mergedContents.contains("\n");
        boolean eol3 = mergedContents.contains("\r");
        Assert.assertTrue(eol1 ^ eol2 ^ eol3);
    }

    /**
     * Tests whether all generics of the original file will be existent after merging
     * @throws IOException
     * @throws MergeException
     * @author mbrunnli (17.06.2013)
     */
    @Test
    public void testMergeWithGenerics() throws IOException, MergeException {
        File baseFile = new File(testFileRootPath + "BaseFile_generics.java");
        File patchFile = new File(testFileRootPath + "PatchFile_generics.java");
        String mergedContents =
            new JavaMerger("", false).merge(baseFile, FileUtils.readFileToString(patchFile), "UTF-8");

        Assert.assertTrue(mergedContents.contains("class Clazz<T extends Object>"));
        Assert.assertTrue(mergedContents.contains("Map<String,T>"));
        Assert.assertTrue(mergedContents.contains("private T t;"));
        Assert.assertTrue(mergedContents.contains("public T get()"));
        Assert.assertTrue(mergedContents.contains("public <U extends Number> void inspect(U u)"));
    }

    /**
     * Tests merging java without adding new lines to method bodies (was a bug)
     * @throws IOException
     * @throws MergeException
     * @author mbrunnli (07.06.2014)
     */
    @Test
    public void testMergeMethodsWithoutExtendingMethodBodyWithWhitespaces() throws IOException,
        MergeException {
        File file = new File(testFileRootPath + "PatchFile_method.java");

        ClassLibraryBuilder classLibraryBuilder = new ModifyableClassLibraryBuilder();
        JavaSource source = classLibraryBuilder.addSource(new FileInputStream(file));
        JavaClass origClazz = (ModifyableJavaClass) source.getClasses().get(0);

        String mergedContents =
            new JavaMerger("", true).merge(file, Files.toString(file, Charset.forName("UTF-8")), "UTF-8");

        classLibraryBuilder = new ModifyableClassLibraryBuilder();
        source = classLibraryBuilder.addSource(new StringReader(mergedContents));
        JavaClass resultClazz = (ModifyableJavaClass) source.getClasses().get(0);

        for (JavaMethod method : resultClazz.getMethods()) {
            JavaMethod origMethod =
                origClazz.getMethodBySignature(method.getName(), method.getParameterTypes());
            Assert.assertEquals(origMethod.getCodeBlock(), method.getCodeBlock());
        }
    }

    /**
     * Calls the {@link JavaMerger} to merge the base and patch file wit the given overriding behavior
     * @param baseFile
     *            base file
     * @param patchFile
     *            patch file
     * @param override
     *            overriding behavior
     * @return the merged {@link JavaSource}
     * @throws IOException
     *             if one of the files could not be read
     * @throws MergeException
     * @author mbrunnli (17.04.2013)
     */
    private JavaSource getMergedSource(File baseFile, File patchFile, boolean override) throws IOException,
        MergeException {
        String mergedContents =
            new JavaMerger("", override).merge(baseFile, FileUtils.readFileToString(patchFile), "UTF-8");
        return getJavaClass(new StringReader(mergedContents)).getSource();
    }

    /**
     * Returns the {@link JavaClass} parsed by the given {@link Reader}
     * @param reader
     *            {@link Reader} which contents should be parsed
     * @return the parsed {@link JavaClass}
     * @author mbrunnli (19.03.2013)
     */
    private ModifyableJavaClass getJavaClass(Reader reader) {
        ClassLibraryBuilder classLibraryBuilder = new ModifyableClassLibraryBuilder();
        classLibraryBuilder.appendDefaultClassLoaders();
        classLibraryBuilder.addSource(reader);
        JavaSource source = null;
        for (JavaSource s : classLibraryBuilder.getClassLibrary().getJavaSources()) {
            source = s;
            // only consider one class per file
            break;
        }
        // save cast as given by the customized builder
        return (ModifyableJavaClass) source.getClasses().get(0);
    }

}
