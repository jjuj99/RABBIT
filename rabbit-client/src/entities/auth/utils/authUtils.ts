import { RefreshTokenAPI } from "@/entities/auth/api/authApi";

// 클로저를 사용한 액세스 토큰 관리
let accessToken: string | null = null;

/**
 * 현재 메모리에 저장된 액세스 토큰을 반환합니다.
 */
export const getAccessToken = () => accessToken;

/**
 * 메모리에 액세스 토큰을 저장합니다.
 */
export const setAccessToken = (token: string) => (accessToken = token);

/**
 * 액세스 토큰을 메모리에서 제거합니다.
 */
export const clearAccessToken = () => {
  accessToken = null;
};

/**
 * 리프레시 토큰을 사용하여 새 액세스 토큰을 요청합니다.
 * 로그인되지 않은 사용자의 경우 null을 반환합니다 (에러를 던지지 않음).
 */
export const refreshAccessToken = async (): Promise<{
  userName: string;
  nickname: string;
  accessToken: string;
} | null> => {
  try {
    const res = await RefreshTokenAPI();

    if (res.status === "ERROR") {
      // 에러를 던지지 않고, 토큰을 클리어하고 null 반환
      clearAccessToken();
      return null;
    }

    if (res.status === "SUCCESS" && res.data) {
      setAccessToken(res.data.accessToken);
      return res.data;
    }

    // 다른 상태코드나 데이터가 없는 경우도 처리
    clearAccessToken();
    return null;
  } catch (error) {
    // 네트워크 오류 등의 예외 상황에서도 토큰을 클리어하고 null 반환
    clearAccessToken();
    console.log("토큰 갱신 중 오류 발생:", error);
    return null;
  }
};

/**
 * 로그인 상태를 확인합니다.
 * 액세스 토큰이 있으면 true, 없으면 리프레시를 시도하고 결과에 따라 반환합니다.
 */
export const checkAuthStatus = async (): Promise<boolean> => {
  // 이미 액세스 토큰이 있는 경우
  if (getAccessToken()) {
    return true;
  }

  // 없으면 리프레시 시도
  const userData = await refreshAccessToken();
  return !!userData; // userData가 있으면 true, 없으면 false
};
