package au.com.quaysystems.qrm.server.servlet.exp;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.dto.ModelRiskProject;
import au.com.quaysystems.qrm.dto.ModelToleranceMatrix;

public class ProbUtil {

	public static void setDynamicProb(ModelRiskProject proj, ModelRisk risk, ModelToleranceMatrix mat, boolean preMit, boolean riskUpdate) {

		double prob = calcProb(risk, preMit);
		if (!riskUpdate){
			prob = prob/100;
		}
		double qprob = (preMit)?mat.maxProb+0.5:1.5;

		if (!riskUpdate){

			if (mat.probVal1 != null && 0 <=prob && prob <= mat.probVal1 && mat.maxProb >= 1) {
				qprob = 1.0+(prob/mat.probVal1);
			}
			if (mat.probVal1 != null && mat.probVal2 !=null && mat.probVal1 < prob && prob <= mat.probVal2 && mat.maxProb >= 2){
				qprob = 2.0+((prob-mat.probVal1)/(mat.probVal2-mat.probVal1));
			}
			if (mat.probVal2 != null && mat.probVal3 !=null && mat.probVal2 < prob && prob <= mat.probVal3 && mat.maxProb >= 3){
				qprob = 3.0+((prob-mat.probVal2)/(mat.probVal3-mat.probVal2));
			}
			if (mat.probVal3 != null && mat.probVal4 !=null && mat.probVal3 < prob && prob <= mat.probVal4 && mat.maxProb >= 4){
				qprob = 4.0+((prob-mat.probVal3)/(mat.probVal4-mat.probVal3));
			}
			if (mat.probVal4 != null && mat.probVal5 !=null && mat.probVal4 < prob && prob <= mat.probVal5 && mat.maxProb >= 5){
				qprob = 5.0+((prob-mat.probVal4)/(mat.probVal5-mat.probVal4));
			}
			if (mat.probVal5 != null && mat.probVal6 !=null && mat.probVal5 < prob && prob <= mat.probVal6 && mat.maxProb >= 6){
				qprob = 6.0+((prob-mat.probVal5)/(mat.probVal6-mat.probVal5));
			}
			if (mat.probVal6 != null && mat.probVal7 !=null && mat.probVal6 < prob && prob <= mat.probVal7 && mat.maxProb >= 7){
				qprob = 7.0+((prob-mat.probVal6)/(mat.probVal7-mat.probVal6));
			}
			if (mat.probVal7 != null && mat.probVal8 !=null && mat.probVal7 < prob && prob <= mat.probVal8 && mat.maxProb == 8){
				qprob = 8.0+((prob-mat.probVal7)/(mat.probVal8-mat.probVal7));
			}

			if (preMit){
				risk.inherentProb = qprob;
			} else {
				risk.treatedProb = qprob;
			}
		} else {
			// The the risk likelihood parameters to match the matrix settings.
			qprob = (preMit)?risk.inherentProb:risk.treatedProb;

			double lowerLimit = 0.0;
			double upperLimit = 0.0;
			switch(new Double(Math.floor(qprob)).intValue()){
			case 1:
				lowerLimit = 0.0;
				upperLimit = mat.probVal1;
				break;
			case 2:
				lowerLimit = mat.probVal1;
				upperLimit = mat.probVal2;
				break;
			case 3:
				lowerLimit = mat.probVal2;
				upperLimit = mat.probVal3;
				break;
			case 4:
				lowerLimit = mat.probVal3;
				upperLimit = mat.probVal4;
				break;
			case 5:
				lowerLimit = mat.probVal4;
				upperLimit = mat.probVal5;
				break;
			case 6:
				lowerLimit = mat.probVal5;
				upperLimit = mat.probVal6;
				break;
			case 7:
				lowerLimit = mat.probVal6;
				upperLimit = mat.probVal7;
				break;
			case 8:
				lowerLimit = mat.probVal7;
				upperLimit = mat.probVal8;
				break;
			}

			prob = lowerLimit+(upperLimit - lowerLimit)*(qprob - Math.floor(qprob));

			if (preMit){
				risk.likeprob = prob;
				risk.liketype = 4;
			} else {
				risk.likepostProb = prob;
				risk.likepostType = 4;
			}

		}
	}
	
	public static double calcProb(ModelRisk risk, boolean preMit){

		long days = (risk.endExposure.getTime() - risk.startExposure.getTime()) / (1000 * 60 * 60 * 24);
		double alpha;
		double T;
		int type;

		if (preMit){
			type = risk.liketype;
		} else {
			type = risk.likepostType;
		}

		try {
			if (type == 4) {
				if (preMit){
					return risk.likeprob*100;
				} else {
					return risk.likepostProb*100;
				}
			} else {

				if (preMit){
					alpha = risk.likealpha;
					T = risk.liket;
					type = risk.liketype;
				} else {
					alpha = risk.likepostAlpha;
					T = risk.likepostT;
					type = risk.likepostType;
				}

				double alphat = alpha * (days / T);
				Double prob = 1 - (Math.exp(-alphat) * ((Math.pow(alphat, 0) / QRMConstants.fact(0))));
				return prob*100;
			}
		} catch (Exception e) {
			return -1.0;
		}
	}
	
	public static void setDynamicProb2(ModelRisk risk, ModelToleranceMatrix mat, boolean preMit) {

		double qprob = (preMit)?mat.maxProb+0.5:1.5;


		// The the risk likelihood parameters to match the matrix settings.
		qprob = (preMit)?risk.inherentProb:risk.treatedProb;

		double lowerLimit = 0.0;
		double upperLimit = 0.0;
		switch(new Double(Math.floor(qprob)).intValue()){
		case 1:
			lowerLimit = 0.0;
			upperLimit = mat.probVal1;
			break;
		case 2:
			lowerLimit = mat.probVal1;
			upperLimit = mat.probVal2;
			break;
		case 3:
			lowerLimit = mat.probVal2;
			upperLimit = mat.probVal3;
			break;
		case 4:
			lowerLimit = mat.probVal3;
			upperLimit = mat.probVal4;
			break;
		case 5:
			lowerLimit = mat.probVal4;
			upperLimit = mat.probVal5;
			break;
		case 6:
			lowerLimit = mat.probVal5;
			upperLimit = mat.probVal6;
			break;
		case 7:
			lowerLimit = mat.probVal6;
			upperLimit = mat.probVal7;
			break;
		case 8:
			lowerLimit = mat.probVal7;
			upperLimit = mat.probVal8;
			break;
		}

		double prob = lowerLimit+(upperLimit - lowerLimit)*(qprob - Math.floor(qprob));

		if (preMit){
			risk.likeprob = prob;
			risk.liketype = 4;
		} else {
			risk.likepostProb = prob;
			risk.likepostType = 4;
		}


	}


}
