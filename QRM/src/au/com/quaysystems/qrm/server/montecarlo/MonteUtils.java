package au.com.quaysystems.qrm.server.montecarlo;

import java.util.Date;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.dto.ModelRisk;

public class MonteUtils {

	public static double calcDaysOverlap(final Date itStartDate,
			final Date itEndDate, final ModelRisk risk) {

		if ((itStartDate == null) || (itEndDate == null) || (risk == null)) {
			return -1.0;
		}

		long itStart = itStartDate.getTime();
		long itEnd = itEndDate.getTime();
		long riskStart = (risk.getBeginExposure()).getTime();
		long riskEnd = (risk.getEndExposure()).getTime();

		if (itEnd <= riskStart || itStart >= riskEnd) {
			return 0.0;
		}
		if ((itStart <= riskStart) && (riskEnd <= itEnd)) {
			return (riskEnd - riskStart) / QRMConstants.DAYSMSEC;
		}

		if ((riskStart < itStart) && (riskEnd > itStart) && (riskEnd <= itEnd)) {
			return (riskEnd - itStart) / QRMConstants.DAYSMSEC;
		}

		if ((riskStart < itEnd) && (riskStart >= itStart) && (riskEnd > itEnd)) {
			return (itEnd - riskStart) / QRMConstants.DAYSMSEC;
		}
		if ((riskStart <= itStart) && (riskEnd >= itEnd)) {
			return (itEnd - itStart) / QRMConstants.DAYSMSEC;
		}

		return 0.0;
		// Determine the overlap period.
	}
	public static double calcPoisson(final Date startExposure,
			final Date endExposure, final double alpha, final double T,	final int k) {

		long diff = endExposure.getTime() - startExposure.getTime();
		double days = diff / (1000 * 60 * 60 * 24);
		double t = days / T;
		double alphat = alpha * t;
		double prob = Math.exp(-alphat) * ((Math.pow(alphat, k) / QRMConstants.fact(k)));

		return prob;

	}
	public static double calcPoisson(final double days, final double alpha,
			final double T, final int k) {

		double t = days / T;
		double alphat = alpha * t;
		double kfact = QRMConstants.fact(k);
		double prob = Math.exp(-alphat) * ((Math.pow(alphat, k) / kfact));

		return prob;

	}
	public static double calcPostRiskProb(final Liklihood like,	final Date itStartDate, final Date itEndDate, final ModelRisk risk) {
		double prob0 = 0;

		try {
			if (like.getPostType() == 4) { //$NON-NLS-1$
				return calcPropOverlap(itStartDate, itEndDate, risk) * (like.getPostProb() / 100);
			}
		} catch (RuntimeException e1) {
			return 0;
		}

		try {
			prob0 = calcPoisson(calcDaysOverlap(itStartDate, itEndDate, risk), like.getPostAlpha(), like.getPostT(), 0);
		} catch (RuntimeException e) {
			prob0 = 0.0;
		}

		return 1 - prob0;
	}
	public static double calcPreRiskProb(final Liklihood like, final Date itStartDate, final Date itEndDate, final ModelRisk risk) {

		double prob0 = 0;

		try {
			if (like.getType() == 4) { //$NON-NLS-1$
				return calcPropOverlap(itStartDate, itEndDate, risk) * (like.getProb() / 100);
			}
		} catch (RuntimeException e1) {
			return 0;
		}

		try {
			prob0 = MonteUtils.calcPoisson(calcDaysOverlap(itStartDate, itEndDate, risk), like.getAlpha(), like	.getT(), 0);
		} catch (RuntimeException e) {
			prob0 = 0.0;
		}

		return 1 - prob0;

	}
	public static double calcPropOverlap(final Date itStartDate, final Date itEndDate, final ModelRisk risk) {

		if ((itStartDate == null) || (itEndDate == null) || (risk == null)) {
			return -1.0;
		}

		long itStart = itStartDate.getTime();
		long itEnd = itEndDate.getTime();
		long riskStart = (risk.getBeginExposure()).getTime();
		long riskEnd = (risk.getEndExposure()).getTime();

		if (itEnd <= riskStart) {
			return 0.0;
		}
		if (itStart >= riskEnd) {
			return 0.0;
		}
		if ((itStart <= riskStart) && (riskEnd <= itEnd)) {
			return 1.0;
		}
		if ((riskStart < itStart) && (riskEnd > itStart) && (riskEnd <= itEnd)) {
			return (double) (riskEnd - itStart)	/ (double) (riskEnd - riskStart);
		}

		if ((riskStart < itEnd) && (riskStart >= itStart) && (riskEnd > itEnd)) {
			return ((double) (itEnd - riskStart) / (double) (riskEnd - riskStart));
		}

		if ((riskStart <= itStart) && (riskEnd >= itEnd)) {
			return ((double) (itEnd - itStart) / (double)(riskEnd - riskStart));
		}

		return 0.0;
	}
}
