import axios from 'axios';

// S3 전용 요청 클라이언트
const s3Client = axios.create({
    headers: {
        'Content-Type': 'application/octet-stream'
    }
});

export default s3Client;