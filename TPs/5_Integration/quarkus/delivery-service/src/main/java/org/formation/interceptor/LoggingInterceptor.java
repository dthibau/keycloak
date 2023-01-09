package org.formation.interceptor;


import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.jboss.logging.Logger;


@Logged 
@Priority(2020) 
@Interceptor 
public class LoggingInterceptor {

	@Inject
	Logger logger;

	@AroundInvoke
	Object logInvocation(InvocationContext context) throws Exception {
		long start = System.currentTimeMillis();
		logger.info(context.getMethod() + " starting");
		
		Object ret = context.proceed();
		
		logger.info(context.getMethod() + " returning in " + (System.currentTimeMillis()-start) + "ms");
		return ret;
	}
}
