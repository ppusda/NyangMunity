# 1단계: 빌드 환경 설정
FROM node:18-alpine AS build

# 작업 디렉토리 설정
WORKDIR /app

# 의존성 파일 복사
COPY package.json package-lock.json ./

# 의존성 설치
RUN npm install

# 애플리케이션 소스 복사
COPY . .

# Vue 애플리케이션 빌드
RUN npm run build

# 2단계: 실행 환경 설정
FROM node:18-alpine

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 스테이지에서 생성된 파일 복사
COPY --from=build /app/dist .

# 애플리케이션 포트 설정
EXPOSE 8021

# 환경변수 설정
ENV NODE_ENV=production
ENV PORT=8021

# 정적 파일을 제공하기 위해 serve 패키지 설치
RUN npm install -g serve

# 애플리케이션 실행
CMD ["serve", "-s", ".", "-l", "8021"]
