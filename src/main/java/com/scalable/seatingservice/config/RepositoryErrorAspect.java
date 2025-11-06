package com.scalable.seatingservice.config;

import com.scalable.seatingservice.entity.SeatsApiResponse;
import com.scalable.seatingservice.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class RepositoryErrorAspect {

    @Around("execution(* com.scalable.seatingservice.controller..*(..))")
    public Object handlePostgresRepositoryErrors(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();

        } catch (DataIntegrityViolationException ex) {
            log.error("Data integrity violation in {}: {}", joinPoint.getSignature(), ex.getMessage());
            return buildResponse(
                    HttpStatus.CONFLICT,
                    "PG_INTEGRITY_VIOLATION",
                    "Database constraint violated — duplicate or invalid reference."
            );

        } catch (CannotAcquireLockException ex) {
            log.error("Lock acquisition failure in {}: {}", joinPoint.getSignature(), ex.getMessage());
            return buildResponse(
                    HttpStatus.CONFLICT,
                    "PG_LOCK_ERROR",
                    "The resource is locked due to concurrent modification. Please retry."
            );

        } catch (QueryTimeoutException ex) {
            log.error("Query timeout in {}: {}", joinPoint.getSignature(), ex.getMessage());
            return buildResponse(
                    HttpStatus.REQUEST_TIMEOUT,
                    "PG_QUERY_TIMEOUT",
                    "Database query took too long. Try again later."
            );

        } catch (CannotGetJdbcConnectionException ex) {
            // Database unreachable (network / pool issue)
            log.error("PostgreSQL connection failure in {}: {}", joinPoint.getSignature(), ex.getMessage());
            return buildResponse(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "PG_CONNECTION_ERROR",
                    "Database connection unavailable. Please try again later."
            );

        } catch (BadSqlGrammarException ex) {
            // SQL syntax or schema mismatch — typically a server bug
            log.error("SQL syntax or mapping error in {}: {}", joinPoint.getSignature(), ex.getMessage());
            return buildResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "PG_SQL_ERROR",
                    "Internal query or mapping error. Please contact support."
            );

        } catch (DataAccessResourceFailureException ex) {
            log.error("Database resource failure in {}: {}", joinPoint.getSignature(), ex.getMessage());
            return buildResponse(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "PG_RESOURCE_FAILURE",
                    "Database resource temporarily unavailable."
            );

        } catch (DataAccessException ex) {
            log.error("Data access error in {}: {}", joinPoint.getSignature(), ex.getMessage());
            return buildResponse(
                    HttpStatus.BAD_REQUEST,
                    "PG_DATA_ACCESS_ERROR",
                    "Invalid or unexpected data access operation."
            );

        }
        catch (BaseException ex) {
            log.error("Data access error in {}: {}", joinPoint.getSignature(), ex.getMessage());
            return buildResponse(
                    HttpStatus.BAD_REQUEST,
                    ex.getCode(),
                    ex.getMessage()
            );

        }
        catch (Exception ex) {
            log.error("Unexpected PostgreSQL error in {}: {}", joinPoint.getSignature(), ex.getMessage(), ex);
            return buildResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "PG_UNEXPECTED_ERROR",
                    "An unexpected database error occurred."
            );
        }
    }

    private ResponseEntity<SeatsApiResponse<?>> buildResponse(HttpStatus status, String code, String message) {
        SeatsApiResponse<?> response = new SeatsApiResponse<>(code, message);
        return ResponseEntity.status(status).body(response);
    }

}

