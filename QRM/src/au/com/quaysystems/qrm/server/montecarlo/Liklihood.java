package au.com.quaysystems.qrm.server.montecarlo;

@SuppressWarnings("serial")
public class Liklihood implements java.io.Serializable {
	
	private Integer type;
	private Double prob;
	private Double alpha;
	private Double t;
	private Integer postType;
	private Double postProb;
	private Double postAlpha;
	private Double postT;
	
	public Liklihood() {}
	public Liklihood(final Double Alpha, final Double Prob, final Double T, final Integer Type, final Double postAlpha, final Double postProb, final Double postT, final Integer postType) {
		setAlpha(Alpha);
		setProb(Prob);
		setT(T);
		setType(Type);
		setPostAlpha(postAlpha);
		setPostProb(postProb);
		setPostT(postT);
		setPostType(postType);
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Double getProb() {
		return prob;
	}
	public void setProb(Double prob) {
		this.prob = prob;
	}
	public Double getAlpha() {
		return alpha;
	}
	public void setAlpha(Double alpha) {
		this.alpha = alpha;
	}
	public Double getT() {
		return t;
	}
	public void setT(Double t) {
		this.t = t;
	}
	public Integer getPostType() {
		return postType;
	}
	public void setPostType(Integer postType) {
		this.postType = postType;
	}
	public Double getPostProb() {
		return postProb;
	}
	public void setPostProb(Double postProb) {
		this.postProb = postProb;
	}
	public Double getPostAlpha() {
		return postAlpha;
	}
	public void setPostAlpha(Double postAlpha) {
		this.postAlpha = postAlpha;
	}
	public Double getPostT() {
		return postT;
	}
	public void setPostT(Double postT) {
		this.postT = postT;
	}

}
