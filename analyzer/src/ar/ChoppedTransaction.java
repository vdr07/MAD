package ar;

public @interface ChoppedTransaction {
	String originalTransaction() default "";
	String microservice();
}
