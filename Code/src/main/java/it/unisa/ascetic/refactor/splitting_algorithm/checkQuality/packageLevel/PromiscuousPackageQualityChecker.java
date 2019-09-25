package it.unisa.ascetic.refactor.splitting_algorithm.checkQuality.packageLevel;

import it.unisa.ascetic.analysis.code_smell_detection.similarityComputation.CosineSimilarity;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.MethodBean;
import it.unisa.ascetic.storage.beans.PackageBean;

import java.io.IOException;
import java.util.Collection;


public class PromiscuousPackageQualityChecker {

	private double maxIpc = 0.0;
	private double minIpc = 0.0;

	public double computeICP(ClassBean pFirstClass, ClassBean pSecondClass, PackageBean pPackage) {
		this.computeMaxAndMinICP(pPackage);

		double icp=0.0;
		double normalizedIcp=0.0;

		int dependenciesFirstToSecond = this.computeWeigthedNumberOfDependencies(pFirstClass, pSecondClass);
		int dependenciesSecondToFirst = this.computeWeigthedNumberOfDependencies(pSecondClass, pFirstClass);

		if(dependenciesFirstToSecond > dependenciesSecondToFirst) {
			icp = dependenciesFirstToSecond;
		} else icp = dependenciesSecondToFirst;

		if( (this.maxIpc - this.minIpc) > 0.0 )
			normalizedIcp = (icp - this.minIpc) / (this.maxIpc - this.minIpc);

		if(normalizedIcp < 0.0005)
			normalizedIcp = 0.0;
		
		return normalizedIcp; 
	}

	public double computeCCBC(ClassBean pFirstClass, ClassBean pSecondClass) throws IOException {
		double ccbc=0.0;
		double ccm=0.0;

		for(MethodBean methodBean: pFirstClass.getMethodList()) {

			for(MethodBean methodBeanToCompare: pSecondClass.getMethodList()) {

				String[] documentOne = new String[2];
				documentOne[0] = methodBean.getFullQualifiedName();
				documentOne[1] = methodBean.getTextContent();

				String[] documentTwo = new String[2];
				documentTwo[0] = methodBeanToCompare.getFullQualifiedName();
				documentTwo[1] = methodBeanToCompare.getTextContent();

				CosineSimilarity cosineSimilarity = new CosineSimilarity();
				ccm += cosineSimilarity.computeSimilarity(documentOne, documentTwo);

			}

		}

		if((pFirstClass.getMethodList().size() * pSecondClass.getMethodList().size()) > 0)
			ccbc = ccm / (pFirstClass.getMethodList().size() * pSecondClass.getMethodList().size());
		
		if(ccbc < 0.0005)
			ccbc = 0.0;
		
		return ccbc;
	}

	private void computeMaxAndMinICP(PackageBean pPackage) {
		double icp=0.0;

		for(ClassBean classBean: pPackage.getClassList()) {

			for(ClassBean classBeanToCompare: pPackage.getClassList()) {

				if(! classBean.getFullQualifiedName().equals(classBeanToCompare.getFullQualifiedName())) {

					int dependenciesFirstToSecond = this.computeWeigthedNumberOfDependencies(classBean, classBeanToCompare);
					int dependenciesSecondToFirst = this.computeWeigthedNumberOfDependencies(classBeanToCompare, classBean);

					if(dependenciesFirstToSecond > dependenciesSecondToFirst) {
						icp = dependenciesFirstToSecond;
					} else icp = dependenciesSecondToFirst;

					if(this.maxIpc < icp)
						maxIpc = icp;

					if(this.minIpc > icp)
						minIpc = icp;
				}
			}

		}
	}

	private int computeWeigthedNumberOfDependencies(ClassBean pFirstClass, ClassBean pSecondClass) {
		int dependencies=0;
		int numberOfParameters=0;

		for(MethodBean methodBean: pFirstClass.getMethodList()) {
			Collection<MethodBean> calls = methodBean.getMethodsCalls();

			for(MethodBean call: calls) {

				for(MethodBean methodBeanToCompare: pSecondClass.getMethodList()) {
					if(call.getFullQualifiedName().equals(methodBeanToCompare.getFullQualifiedName())) {
						numberOfParameters += call.getParameters().size();
						dependencies++;

					}
				}
			}
		}

		return (numberOfParameters*dependencies);
	}
}
