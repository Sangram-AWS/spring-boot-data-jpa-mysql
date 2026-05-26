# Construction Workforce HRMS Backend

## Forked HRMS Project

Forked HRMS backend focused on workforce attendance, overtime tracking, Redis caching, and settlement workflows.

---

# Why This HRMS?

I selected this HRMS because construction workforce management involves real-time attendance tracking, payroll accuracy, overtime settlement, and scalability challenges that closely match real backend engineering problems.

---

# Tech Stack

* Java 21
* Spring Boot 3
* PostgreSQL (Supabase)
* Redis
* Hibernate / JPA
* Maven

---

# Setup Instructions

## 1. Clone Repository

git clone <repo-url>

cd spring-boot-data-jpa-mysql

---

## 2. Configure Environment Variables

Windows PowerShell:

$env:DB_USERNAME="postgres"

$env:DB_PASSWORD="your-password"

---

## 3. Start Redis

WSL:

redis-server

Verify:

redis-cli ping

---

## 4. Run Application

mvn spring-boot:run

---

# API Endpoints

## Attendance

POST /api/attendance/clock-in

POST /api/attendance/clock-out

GET /api/attendance/active

GET /api/attendance/log

---

## Overtime

GET /api/overtime/summary/{workerId}

POST /api/overtime/settle/{workerId}

---

# Sample curl Commands

## Clock In

curl -X POST http://localhost:8080/api/attendance/clock-in
-H "Content-Type: application/json"
-d "{"workerId":1,"siteId":2}"

## Active Workers

curl http://localhost:8080/api/attendance/active

---

# Business Rules

* Overtime starts after 8 hours/day
* First 2 overtime hours use 1.5x rate
* Remaining overtime uses 2x rate
* Monthly overtime cap = 60 hours
* Current month cannot be settled
* No duplicate clock-ins
* No partial settlement allowed

---

# Redis Caching Strategy

Redis is used for active worker caching because site supervisors need extremely fast worker lookup during shift starts.

The system gracefully falls back to PostgreSQL if Redis becomes unavailable.

---

# Performance Optimizations

* Pagination for attendance logs
* EntityGraph to prevent N+1 queries
* HikariCP connection pooling
* Redis caching
* Transactional settlement handling

---

# LF Ticket Fixes

## LF-201

Added configurable CORS support.

## LF-202

Implemented graceful Redis degradation.

## LF-203

Implemented pagination and fixed N+1 query issue using EntityGraph.

## LF-204

Implemented transactional settlement handling.

## LF-205

Optimized HikariCP connection pooling configuration.

---

# AI Tools Used

## ChatGPT

Used for:

* architecture guidance
* debugging support
* Redis integration understanding
* pagination optimization
* transactional flow improvements

## Claude

Used for:

* brainstorming architecture decisions
* understanding business constraints
* refining overtime settlement flow

---

# Design Decisions

## Why Redis?

Active worker lookup requires low latency during shift start operations.

## Why Transactions?

Settlement cannot be partially completed because payroll depends on consistent data.

## Why Pagination?

Attendance logs can grow very large across many construction sites.

## What I'd Improve With More Time

* JWT authentication
* Role-based authorization
* Docker deployment
* Kafka-based event processing
* Monitoring and metrics
* Automated testing