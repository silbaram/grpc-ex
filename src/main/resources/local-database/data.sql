-- 예제 데이터 삽입 DML
INSERT INTO ERROR_MESSAGE (ERROR_CODE, LANG, MESSAGE) VALUES
('E401', 'ko', '인증되지 않은 사용자입니다. 로그인이 필요합니다.'),
('E401', 'en', 'Unauthorized. Please log in.'),

('E403', 'ko', '접근 권한이 없습니다.'),
('E403', 'en', 'You do not have permission to access this resource.'),

('E404', 'ko', '요청하신 리소스를 찾을 수 없습니다.'),
('E404', 'en', 'The requested resource was not found.'),

('E500', 'ko', '서버에 일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.'),
('E500', 'en', 'An unexpected server error occurred. Please try again later.'),

('V100', 'ko', '입력값이 올바르지 않습니다. 다시 확인해주세요.'),
('V100', 'en', 'Invalid input. Please check your data.');