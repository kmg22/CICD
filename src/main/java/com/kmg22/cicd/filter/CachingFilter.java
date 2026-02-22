package com.kmg22.cicd.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Component
@Order(1)
public class CachingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        CachedBodyRequestWrapper wrappedRequest = new CachedBodyRequestWrapper(request);
        CachedBodyResponseWrapper wrappedResponse = new CachedBodyResponseWrapper(response);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            wrappedResponse.copyBodyToResponse();
        }
    }

    // Request Body 캐싱
    public static class CachedBodyRequestWrapper extends jakarta.servlet.http.HttpServletRequestWrapper {
        private final byte[] cachedBody;

        public CachedBodyRequestWrapper(HttpServletRequest request) throws IOException {
            super(request);
            this.cachedBody = request.getInputStream().readAllBytes();
        }

        public byte[] getCachedBody() {
            return cachedBody;
        }

        @Override
        public ServletInputStream getInputStream() {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cachedBody);
            return new ServletInputStream() {
                public int read() { return byteArrayInputStream.read(); }
                public boolean isFinished() { return byteArrayInputStream.available() == 0; }
                public boolean isReady() { return true; }
                public void setReadListener(ReadListener listener) {}
            };
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
        }
    }

    // Response Body 캐싱
    public static class CachedBodyResponseWrapper extends jakarta.servlet.http.HttpServletResponseWrapper {
        private final ByteArrayOutputStream cachedBody = new ByteArrayOutputStream();
        private PrintWriter writer;
        private ServletOutputStream outputStream;

        public CachedBodyResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            if (writer == null) {
                writer = new PrintWriter(new OutputStreamWriter(cachedBody, StandardCharsets.UTF_8));
            }
            return writer;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (outputStream == null) {
                outputStream = new ServletOutputStream() {
                    @Override
                    public void write(int b) {
                        cachedBody.write(b);
                    }
                    @Override
                    public boolean isReady() { return true; }
                    @Override
                    public void setWriteListener(WriteListener listener) {}
                };
            }
            return outputStream;
        }

        public byte[] getCachedBody() {
            if (writer != null) writer.flush();
            return cachedBody.toByteArray();
        }

        public void copyBodyToResponse() throws IOException {
            byte[] body = getCachedBody();
            if (body.length > 0) {
                getResponse().getOutputStream().write(body);
            }
        }
    }
}