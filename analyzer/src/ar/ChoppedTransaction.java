package ar;

public @interface ChoppedTransaction {
	String originalTransaction();
	String microservice();
}
