
package au.com.quaysystems.qrm.server.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;



@WebFilter(urlPatterns = {
		"/images/paintMetric/*",
		"/QRMReportUpload/*",
		"/getConsequenceImage/*",
		"/QRMLikelihood/*",
		"/RiskMatrixPainter/*",
		"/QRMAttachment/*",
		"/QRMComment/*",
		"/QRMMSFormat/*",
		"/getUserProjects",
		"/getRisk",
		"/getRiskProject",
		"/getRiskLiteFetch",
		"/getRiskLiteRPC",
		"/getAllIncidentsSummary",
		"/getWelcomeData",
		"/getMetrics",
		"/getOverviewData",
		"/getCalChart",
		"/getAllUsers",
		"/getRegisterReports",
		"/getRiskReports",
		"/getRiskObjectives",
		"/getIncident", 
		"/getIncidentUpdates", 
		"/getRiskControls",
		"/getRiskCode",
		"/getConsequence",
		"/getRiskConsequences", 
		"/getProjectObjectives",
		"/getContextReports",
		"/getRiskLiteFetchSorted",
		"/getRiskMitigationUpdate",
		"/updateRanksRPC",
		"/updateFamilyRPC",
		"/deleteImportTemplate",
		"/getAnalChart",
		"/getAnalToolsFetch",
		"/getAllUserJobs",
		"/getAllReviews",
		"/getHomePageReports",
		"/getUserScheduledJobs"
		})
public class FilterGZIP implements Filter {

	public final void doFilter(final ServletRequest req,
			final ServletResponse res, final FilterChain chain)
			throws IOException, ServletException {

		// make sure we are dealing with HTTP
		if (req instanceof HttpServletRequest) {
			HttpServletRequest request = (HttpServletRequest) req;	
			HttpServletResponse response = (HttpServletResponse) res;
			String ae = request.getHeader("accept-encoding");
			if ((ae != null) && (ae.indexOf("gzip") != -1)) {
				GZIPResponseWrapper wrappedResponse = new GZIPResponseWrapper(response);
				chain.doFilter(req, wrappedResponse);
				wrappedResponse.finishResponse();
				return;
			}
			chain.doFilter(req, res);
		}
	}

	@Override
	public void destroy() {}
	@Override
	public void init(final FilterConfig arg0) throws ServletException {}
	
	
	private class GZIPResponseWrapper extends HttpServletResponseWrapper {
		
		protected HttpServletResponse origResponse = null;
		protected ServletOutputStream stream = null;
		protected PrintWriter writer = null;
		protected int error = 0;

		public GZIPResponseWrapper(final HttpServletResponse response) {
			super(response);
			origResponse = response;
		}


		public final ServletOutputStream createOutputStream() throws IOException {
			return (new GZIPResponseStream(origResponse));
		}

		public final void finishResponse() {
			try {
				if (writer != null) {
					writer.close();
				} else {
					if (stream != null) {
						stream.close();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public final void flushBuffer() throws IOException {
			stream.flush();
		}


		@Override
		public final ServletOutputStream getOutputStream() throws IOException {
			if (writer != null) {
				throw new IllegalStateException(
						"getWriter() has already been called!");
			}

			if (stream == null) {
				stream = createOutputStream();
			}

			return (stream);
		}

		@Override
		public final PrintWriter getWriter() throws IOException {
			// If access denied, don't create new stream or write because
			// it causes the web.xml's 403 page to not render
			if (this.error == HttpServletResponse.SC_FORBIDDEN) {
				return super.getWriter();
			}

			if (writer != null) {
				return (writer);
			}

			if (stream != null) {
				throw new IllegalStateException(
						"getOutputStream() has already been called!");
			}

			stream = createOutputStream();
			writer = new PrintWriter(new OutputStreamWriter(stream, origResponse
					.getCharacterEncoding()));

			return (writer);
		}

		@Override
		public void setContentLength(final int length) {
			// no action here
		}

		@Override
		public final void sendError(final int err, final String message)
				throws IOException {
			super.sendError(err, message);
			this.error = err;

		}
	}
	
	
	private class GZIPResponseStream extends ServletOutputStream {
		protected OutputStream bufferedOutput = null;
		protected boolean closed = false;
		protected HttpServletResponse response = null;
		protected ServletOutputStream output = null;

		private final int bufferSize = 50000;

		public GZIPResponseStream(final HttpServletResponse response)
				throws IOException {
			super();
			closed = false;
			this.response = response;
			this.output = response.getOutputStream();
			bufferedOutput = new ByteArrayOutputStream();
		}

		@Override
		public final void close() throws IOException {

			// if we buffered everything in memory, gzip it
			if (bufferedOutput instanceof ByteArrayOutputStream) {
				// get the content
				ByteArrayOutputStream baos = (ByteArrayOutputStream) bufferedOutput;

				// prepare a gzip stream
				ByteArrayOutputStream compressedContent = new ByteArrayOutputStream();
				GZIPOutputStream gzipstream = new GZIPOutputStream(
						compressedContent);
				byte[] bytes = baos.toByteArray();
				gzipstream.write(bytes);
				gzipstream.finish();

				// get the compressed content
				byte[] compressedBytes = compressedContent.toByteArray();

				// set appropriate HTTP headers
				response.setContentLength(compressedBytes.length);
				response.addHeader("Content-Encoding", "gzip");
				output.write(compressedBytes);
				try {
					output.flush();
				} catch (Exception e) {
					// Do Nothing
				}
				try {
					output.close();
				} catch (Exception e) {
					// Do Nothing
				}
				closed = true;
			}
			// if things were not buffered in memory, finish the GZIP stream and
			// response
			else if (bufferedOutput instanceof GZIPOutputStream) {
				// cast to appropriate type
				GZIPOutputStream gzipstream = (GZIPOutputStream) bufferedOutput;

				// finish the compression
				gzipstream.finish();

				// finish the response
				output.flush();
				output.close();
				closed = true;
			}
		}

		@Override
		public final void flush() throws IOException {
			if (closed) {
				throw new IOException("Cannot flush a closed output stream");
			}

			bufferedOutput.flush();
		}

		@Override
		public final void write(final int b) throws IOException {
			if (closed) {
				throw new IOException("Cannot write to a closed output stream");
			}

			// make sure we aren't over the buffer's limit
			checkBufferSize(1);

			// write the byte to the temporary output
			bufferedOutput.write((byte) b);
		}

		private void checkBufferSize(final int length) throws IOException {
			// check if we are buffering too large of a file
			if (bufferedOutput instanceof ByteArrayOutputStream) {
				ByteArrayOutputStream baos = (ByteArrayOutputStream) bufferedOutput;

				if ((baos.size() + length) > bufferSize) {
					response.addHeader("Content-Encoding", "gzip");
					byte[] bytes = baos.toByteArray();

					GZIPOutputStream gzipstream = new GZIPOutputStream(output);
					gzipstream.write(bytes);

					bufferedOutput = gzipstream;
				}
			}
		}

		@Override
		public final void write(final byte[] b) throws IOException {
			write(b, 0, b.length);
		}

		@Override
		public final void write(final byte[] b, final int off, final int len)
				throws IOException {

			if (closed) {
				throw new IOException("Cannot write to a closed output stream");
			}

			checkBufferSize(len);

			bufferedOutput.write(b, off, len);
		}
	}

}
