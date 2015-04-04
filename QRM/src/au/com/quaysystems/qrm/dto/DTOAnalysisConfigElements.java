/*
 * 
 */
package au.com.quaysystems.qrm.dto;

// TODO: Auto-generated Javadoc
/**
 * The Class DTOAnalysisConfigElements.
 */
public class DTOAnalysisConfigElements {

	/** The b reverse. */
	public boolean bReverse;

	/** The title. */
	public String title;

	/** The clazz. */
	public String clazz;

	/** The param1. */
	public String param1;

	/** The b3 d. */
	public boolean b3D;

	/** The b tol. */
	public boolean bTol;

	/** The b matrix. */
	public boolean bMatrix;

	/** The b context. */
	public boolean bContext;

	/** The b descend. */
	public boolean bDescend;

	/** The b num elem. */
	public boolean bNumElem;

	/**
	 * Instantiates a new dTO analysis config elements.
	 * 
	 * @param title
	 *            the title
	 * @param clazz
	 *            the clazz
	 * @param param1
	 *            the param1
	 * @param b3d
	 *            the b3d
	 * @param tol
	 *            the tol
	 * @param matrix
	 *            the matrix
	 * @param context
	 *            the context
	 * @param descend
	 *            the descend
	 * @param numElem
	 *            the num elem
	 * @param reverse
	 *            the reverse
	 */
	public DTOAnalysisConfigElements(final String title, final String clazz,
			final String param1, final boolean b3d, final boolean tol,
			final boolean matrix, final boolean context, final boolean descend,
			final boolean numElem, final boolean reverse) {
		super();
		this.title = title;
		this.clazz = clazz;
		this.param1 = param1;
		b3D = b3d;
		bTol = tol;
		bMatrix = matrix;
		bContext = context;
		bDescend = descend;
		this.bNumElem = numElem;
		this.bReverse = reverse;
	}
}
