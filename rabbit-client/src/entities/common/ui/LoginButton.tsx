const LoginButton = () => {
  const handleLogin = () => {
    // 추후 로그인 모달 활상화 버튼
    console.log("로그인");
  };
  return <button onClick={handleLogin}>로그인</button>;
};

export default LoginButton;
