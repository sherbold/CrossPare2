// Copyright 2015 Georg-August-Universität Göttingen, Germany
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package de.ugoe.cs.cpdp.dataprocessing;

import org.apache.commons.collections4.list.SetUniqueList;

import de.ugoe.cs.cpdp.versions.SoftwareVersion;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Standardization procedure after Watanabe et al.: Adapting a Fault Prediction Model to Allow Inter
 * Language Reuse. <br>
 * <br>
 * In comparison to Watanabe et al., we transform training data instead of the test data. Otherwise,
 * this approach would not be feasible with multiple projects.
 * 
 * @author Steffen Herbold
 */
public class AverageStandardization implements ISetWiseProcessingStrategy, IProcessesingStrategy {

    /**
     * Does not have parameters. String is ignored.
     * 
     * @param parameters
     *            ignored
     */
    @Override
    public void setParameter(String parameters) {
        // dummy
    }

    /**
     * @see ISetWiseProcessingStrategy#apply(de.ugoe.cs.cpdp.versions.SoftwareVersion,
     *      org.apache.commons.collections4.list.SetUniqueList)
     */
    @Override
    public void apply(SoftwareVersion testversion, SetUniqueList<SoftwareVersion> trainversionSet) {
        Instances testdata = testversion.getInstances();
        final Attribute classAttribute = testdata.classAttribute();

        final double[] meanTest = new double[testdata.numAttributes()];

        // get means of testdata
        for (int j = 0; j < testdata.numAttributes(); j++) {
            if (testdata.attribute(j) != classAttribute) {
                meanTest[j] = testdata.meanOrMode(j);
            }
        }

        // preprocess training data
        for (SoftwareVersion trainversion : trainversionSet) {
            Instances traindata = trainversion.getInstances();
            double[] meanTrain = new double[testdata.numAttributes()];
            for (int j = 0; j < testdata.numAttributes(); j++) {
                if (testdata.attribute(j) != classAttribute) {
                    meanTrain[j] = traindata.meanOrMode(j);
                }
            }

            for (int i = 0; i < traindata.numInstances(); i++) {
                Instance instance = traindata.instance(i);
                for (int j = 0; j < testdata.numAttributes(); j++) {
                    if (testdata.attribute(j) != classAttribute) {
                    	if(  meanTrain[j]==0.0 ) {
                    		instance.setValue(j, 0.0);
                        } else {
                        	instance.setValue(j, instance.value(j) * meanTest[j] / meanTrain[j]);
                        }
                    }
                }
            }
        }
    }

    /**
     * @see IProcessesingStrategy#apply(de.ugoe.cs.cpdp.versions.SoftwareVersion,
     *      de.ugoe.cs.cpdp.versions.SoftwareVersion)
     */
    @Override
    public void apply(SoftwareVersion testversion, SoftwareVersion trainversion) {
        Instances testdata = trainversion.getInstances();
        Instances traindata = trainversion.getInstances();
        final Attribute classAttribute = testdata.classAttribute();

        final double[] meanTest = new double[testdata.numAttributes()];

        // get means of testdata
        for (int j = 0; j < testdata.numAttributes(); j++) {
            if (testdata.attribute(j) != classAttribute) {
                meanTest[j] = testdata.meanOrMode(j);
            }
        }

        // preprocess training data
        final double[] meanTrain = new double[testdata.numAttributes()];
        for (int j = 0; j < testdata.numAttributes(); j++) {
            if (testdata.attribute(j) != classAttribute) {
                meanTrain[j] = traindata.meanOrMode(j);
            }
        }

        for (int i = 0; i < traindata.numInstances(); i++) {
            Instance instance = traindata.instance(i);
            for (int j = 0; j < testdata.numAttributes(); j++) {
                if (testdata.attribute(j) != classAttribute) {
                    instance.setValue(j, instance.value(j) * meanTest[j] / meanTrain[j]);
                }
            }
        }
    }

}
