package com.gjl.client.compiler.processor;

import com.gjl.client.annotation.Client;
import com.gjl.client.compiler.processor.utils.Logger;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import static com.gjl.client.compiler.processor.utils.Consts.ANNOTATION_TYPE_CLIENT;

/**
 * Created by guojilong on 2018/4/11.
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes(ANNOTATION_TYPE_CLIENT)
public class ClientProcessor extends AbstractProcessor {
    private Elements mElements;
    private Logger logger;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mElements = processingEnv.getElementUtils();
        logger = new Logger(processingEnvironment.getMessager());

        logger.info("init");

    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (CollectionUtils.isNotEmpty(set)) {
            Set<? extends Element> routeElements = roundEnvironment.getElementsAnnotatedWith(Client.class);
            try {

                parseClients(routeElements);

            } catch (Exception e) {
                logger.info(e.getLocalizedMessage());

            }
            return true;
        }
        return false;
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return super.getSupportedAnnotationTypes();
    }

    private void parseClients(Set<? extends Element> routeElements) throws IOException {


        logger.info("start parse");
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder("ClientsManager")
                .addModifiers(Modifier.PUBLIC);


        int clientIndex = 0;
        for (Element routeElement : routeElements) {


            MethodSpec main = MethodSpec.methodBuilder("main")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(void.class)
                    .addParameter(String[].class, "args")
                    .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                    .build();

//            String packgeName = mElements.getPackageOf(routeElements).getQualifiedName().toString();

            FieldSpec fieldSpec = FieldSpec.builder(TypeName.INT, routeElement.getAnnotation(Client.class).name(), Modifier.PUBLIC)
                    .initializer("$L", clientIndex).build();

            clientIndex++;
            classBuilder.addField(fieldSpec);
            //classBuilder.addMethod(main);
        }


        JavaFile javaFile = JavaFile.builder("com.gjl.clientregister", classBuilder.build())
                .build();

        javaFile.writeTo(processingEnv.getFiler());


    }
}
