import { useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/shared/ui/dialog";

// 이미지 경로를 실제 환경에 맞게 조정해야 할 수 있습니다.
interface PassProps {
  userName: string;
  phoneNumber: string;
  onUserNameChange: (value: string) => void;
  onPhoneNumberChange: (value: string) => void;
  onComplete: (phoneNumber: string, name: string) => boolean;
}

const PASS: React.FC<PassProps> = ({
  userName,
  phoneNumber,
  onUserNameChange,
  onPhoneNumberChange,
  onComplete,
}) => {
  const [selectedMobileCo, setSelectedMobileCo] = useState<string>("");
  const [allAgreed, setAllAgreed] = useState<boolean>(false);
  const [agreements, setAgreements] = useState({
    agree1: false,
    agree2: false,
    agree3: false,
    agree4: false,
    agree5: false,
  });
  const [mvnoPopupOpen, setMvnoPopupOpen] = useState<boolean>(false);
  const [selectedMvno, setSelectedMvno] = useState<string>("");
  const [agreePopupOpen, setAgreePopupOpen] = useState<boolean>(false);
  const [currentAgreement, setCurrentAgreement] = useState<string>("");
  const [authPopupOpen, setAuthPopupOpen] = useState<boolean>(false);

  const handleSubmitPass = () => {
    // PASS 인증이 성공했다고 가정하고 (실제로는 PASS API 호출 필요)
    const isSuccess = onComplete(phoneNumber, userName);

    if (isSuccess) {
      // 인증 성공 시 팝업 닫기
      setAuthPopupOpen(false);
    }
    // 실패 시는 팝업 유지 (사용자가 다시 시도할 수 있도록)
  };
  // 통신사 선택 핸들러
  const handleMobileCoSelect = (value: string) => {
    setSelectedMobileCo(value);
    // 알뜰폰(55)인 경우 팝업 표시
    if (value === "55") {
      setMvnoPopupOpen(true);
    } else {
      setSelectedMvno("");
      // MVNO 약관 숨기기
      document.getElementById("mvnoAgree")?.setAttribute("hidden", "hidden");
    }
  };

  // MVNO 선택 핸들러
  const handleMvnoSelect = (value: string) => {
    setSelectedMvno(value);
  };

  // MVNO 팝업 확인 버튼 핸들러
  const handleMvnoConfirm = () => {
    setMvnoPopupOpen(false);
    if (selectedMvno) {
      // MVNO 약관 표시
      document.getElementById("mvnoAgree")?.removeAttribute("hidden");
    }
  };

  // 약관 동의 핸들러
  const handleAgreementChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, checked } = e.target;

    if (name === "agree") {
      // 전체 동의
      setAllAgreed(checked);
      setAgreements({
        agree1: checked,
        agree2: checked,
        agree3: checked,
        agree4: checked,
        agree5: checked,
      });
    } else {
      // 개별 약관 동의
      const newAgreements = { ...agreements, [name]: checked };
      setAgreements(newAgreements);

      // 모든 약관이 동의되었는지 확인
      const allChecked = Object.values(newAgreements).every((value) => value);
      setAllAgreed(allChecked);
    }
  };

  // 약관 팝업 열기 핸들러
  const handleAgreePopup = (agreementId: string) => {
    setCurrentAgreement(agreementId);
    setAgreePopupOpen(true);
  };

  // PASS 인증 핸들러
  const handlePassConfirm = () => {
    setAuthPopupOpen(true);
  };

  // 인증 팝업 입력 핸들러
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    if (name === "userName") {
      onUserNameChange(value);
    } else if (name === "phoneNumber") {
      // 숫자만 입력 가능하도록 처리
      const numericValue = value.replace(/[^0-9]/g, "");
      if (numericValue.length <= 11) {
        onPhoneNumberChange(numericValue);
      }
    }
  };

  // 모든 필수 약관에 동의했는지 확인
  const allRequiredAgreed =
    agreements.agree1 &&
    agreements.agree2 &&
    agreements.agree3 &&
    agreements.agree4;

  return (
    <div className="flex flex-col bg-white">
      {/* Header */}
      <header className="border-b border-gray-200 bg-white p-4">
        <h1 className="flex justify-center">
          <a href="#" className="inline-block">
            <img src="/images/PASS/logo_pass.png" alt="PASS" className="h-8" />
          </a>
        </h1>
      </header>

      {/* Main Content */}
      <section className="mx-auto w-full max-w-md flex-1 p-4">
        <form
          name="authForm"
          method="post"
          autoComplete="off"
          className="w-full"
        >
          <input type="hidden" name="tc" />
          <input type="hidden" name="idcf_mbr_com_cd" value="V33690000000" />
          <input
            type="hidden"
            name="rqst_data"
            value="wfy96lg4J1vupCoI1mfB759MUD3UTUNYQWXa2LMN2bJiH9dzqNjmDWhEAXrMpOOog1O/QUeHetA5jhWRZsoGRaqV5cqzLzTbehx+O1yqzkOuivLFzRnO7Jtbrd3HeQJC+hs7+pcnJcWRMWcarRXAMQ=="
          />
          <input type="hidden" name="target_id" value="" />
          <input type="hidden" name="mbl_tel_cmm_cd" value="" />
          <input type="hidden" name="ableNxKeyUse" value="" />
          <input
            type="hidden"
            name="mobileco"
            id="mobileco"
            value={selectedMobileCo}
          />

          <fieldset className="mb-6">
            <p className="mb-4 text-center text-xl font-bold text-gray-700">
              이용중이신 통신사를 선택해주세요.
            </p>
            <legend className="sr-only">통신사 선택</legend>

            {/* 통신사 선택 버튼 */}
            <ul className="mb-6 grid grid-cols-2 place-items-center gap-2">
              {/* SKT */}
              <li>
                <button
                  type="button"
                  id="agency-skt"
                  value="01"
                  className={`h-40 w-40 rounded-full border-2 ${selectedMobileCo === "01" ? "border-red-500" : "border-gray-300"} rounded`}
                  onClick={() => handleMobileCoSelect("01")}
                >
                  <span className="mx-auto block">
                    <img
                      src={`/images/PASS/logo_sk${selectedMobileCo === "01" ? "" : "_gray"}.png`}
                      alt={`SK telecom ${selectedMobileCo === "01" ? "선택됨" : "선택안됨"}`}
                      className="mx-auto h-8"
                    />
                  </span>
                </button>
              </li>

              {/* KT */}
              <li>
                <button
                  type="button"
                  id="agency-kt"
                  value="02"
                  className={`h-40 w-40 rounded-full border-2 ${selectedMobileCo === "02" ? "border-red-500" : "border-gray-300"} rounded`}
                  onClick={() => handleMobileCoSelect("02")}
                >
                  <span className="mx-auto block">
                    <img
                      src={`/images/PASS/logo_kt${selectedMobileCo === "02" ? "" : "_gray"}.png`}
                      alt={`kt ${selectedMobileCo === "02" ? "선택됨" : "선택안됨"}`}
                      className="mx-auto h-8"
                    />
                  </span>
                </button>
              </li>

              {/* LG U+ */}
              <li>
                <button
                  type="button"
                  id="agency-lgu"
                  value="03"
                  className={`h-40 w-40 rounded-full border-2 ${selectedMobileCo === "03" ? "border-red-500" : "border-gray-300"} rounded`}
                  onClick={() => handleMobileCoSelect("03")}
                >
                  <span className="mx-auto block">
                    <img
                      src={`/images/PASS/logo_lgu${selectedMobileCo === "03" ? "" : "_gray"}.png`}
                      alt={`LG U+ ${selectedMobileCo === "03" ? "선택됨" : "선택안됨"}`}
                      className="mx-auto h-8"
                    />
                  </span>
                </button>
              </li>

              {/* 알뜰폰 */}
              <li>
                <button
                  type="button"
                  id="agency-and"
                  value="55"
                  className={`h-40 w-40 rounded-full border-2 ${selectedMobileCo === "55" ? "border-red-500" : "border-gray-300"} rounded`}
                  onClick={() => handleMobileCoSelect("55")}
                >
                  <span className="mx-auto block">
                    <img
                      src={`/images/PASS/logo_and${selectedMobileCo === "55" ? "" : "_gray"}.png`}
                      alt={`알뜰폰 ${selectedMobileCo === "55" ? "선택됨" : "선택안됨"}`}
                      className="mx-auto h-8"
                    />
                  </span>
                </button>
              </li>
            </ul>

            {/* 전체 동의 */}
            <ul className="mb-2 border-t border-b border-gray-200 py-3">
              <li>
                <div className="flex items-center">
                  <input
                    type="checkbox"
                    name="agree"
                    id="agree_all"
                    checked={allAgreed}
                    onChange={handleAgreementChange}
                    className="mr-2 h-5 w-5"
                  />
                  <label
                    htmlFor="agree_all"
                    className="text-lg font-bold text-gray-800"
                  >
                    전체 동의하기
                  </label>
                </div>
              </li>
            </ul>

            {/* 필수 항목 */}
            <ul className="mb-6 grid grid-cols-2 gap-2">
              <li>
                <div className="flex items-center justify-between">
                  <div className="flex items-center">
                    <input
                      type="checkbox"
                      name="agree1"
                      id="agree1"
                      value="Y"
                      checked={agreements.agree1}
                      onChange={handleAgreementChange}
                      className="mr-2 h-5 w-5"
                    />
                    <label htmlFor="agree1" className="text-sm text-gray-800">
                      개인정보이용동의
                    </label>
                  </div>
                  {/* <button
                    type="button"
                    className="text-sm text-blue-600"
                    onClick={() => handleAgreePopup("agree1")}
                  >
                    보기
                  </button> */}
                </div>
              </li>
              <li>
                <div className="flex items-center justify-between">
                  <div className="flex items-center">
                    <input
                      type="checkbox"
                      name="agree2"
                      id="agree2"
                      value="Y"
                      checked={agreements.agree2}
                      onChange={handleAgreementChange}
                      className="mr-2 h-5 w-5"
                    />
                    <label htmlFor="agree2" className="text-sm text-gray-800">
                      고유식별정보처리동의
                    </label>
                  </div>
                  {/* <button
                    type="button"
                    className="text-sm text-blue-600"
                    onClick={() => handleAgreePopup("agree2")}
                  >
                    보기
                  </button> */}
                </div>
              </li>
              <li>
                <div className="flex items-center justify-between">
                  <div className="flex items-center">
                    <input
                      type="checkbox"
                      name="agree3"
                      id="agree3"
                      value="Y"
                      checked={agreements.agree3}
                      onChange={handleAgreementChange}
                      className="mr-2 h-5 w-5"
                    />
                    <label htmlFor="agree3" className="text-sm text-gray-800">
                      서비스이용약관동의
                    </label>
                  </div>
                  {/* <button
                    type="button"
                    className="text-sm text-blue-600"
                    onClick={() => handleAgreePopup("agree3")}
                  >
                    보기
                  </button> */}
                </div>
              </li>
              <li>
                <div className="flex items-center justify-between">
                  <div className="flex items-center">
                    <input
                      type="checkbox"
                      name="agree4"
                      id="agree4"
                      value="Y"
                      checked={agreements.agree4}
                      onChange={handleAgreementChange}
                      className="mr-2 h-5 w-5"
                    />
                    <label htmlFor="agree4" className="text-sm text-gray-800">
                      통신사이용약관동의
                    </label>
                  </div>
                  {/* <button
                    type="button"
                    className="text-sm text-blue-600"
                    onClick={() => handleAgreePopup("agree4")}
                  >
                    보기
                  </button> */}
                </div>
              </li>
              <li id="mvnoAgree" hidden>
                <div className="flex items-center justify-between">
                  <div className="flex items-center">
                    <input
                      type="checkbox"
                      name="agree5"
                      id="agree5"
                      value="Y"
                      checked={agreements.agree5}
                      onChange={handleAgreementChange}
                      className="mr-2 h-5 w-5"
                    />
                    <label htmlFor="agree5" className="text-sm text-gray-800">
                      제3자 정보제공 동의
                    </label>
                  </div>
                  <button
                    type="button"
                    className="text-sm text-blue-600"
                    onClick={() => handleAgreePopup("agree5")}
                  >
                    보기
                  </button>
                </div>
              </li>
            </ul>

            {/* 버튼 영역 */}
            <div className="space-y-2">
              <button
                type="button"
                id="btnPass"
                className={`w-full rounded py-3 font-bold text-white ${selectedMobileCo && allRequiredAgreed ? "bg-red-600" : "bg-gray-400"}`}
                disabled={!selectedMobileCo || !allRequiredAgreed}
                onClick={handlePassConfirm}
              >
                PASS로 인증하기
              </button>
            </div>
          </fieldset>
        </form>

        {/* 키보드 보안 안내 */}
        <div className="mb-6 rounded bg-gray-100 p-4">
          <p className="flex items-center">
            <input type="checkbox" id="c_install" className="mr-2 h-5 w-5" />
            <label htmlFor="c_install" className="text-sm text-gray-700">
              안전한 본인 확인을 위해 키보드 보안 프로그램을 설치해주세요.
            </label>
          </p>
        </div>
      </section>
      {/* Footer */}
      <footer className="border-t border-gray-200 bg-white px-6 py-4">
        <div className="mx-auto max-w-md">
          <div className="mb-4 flex items-center justify-between">
            <div>
              <img
                src="/images/PASS/certify_mark.png"
                alt="한국 웹접근성 인증평가원 SOFTWARE ACCESSIBILITY 마크"
                className="h-10"
              />
            </div>
            <div className="text-right text-xs text-gray-600">
              <a href="#" className="mr-2">
                이용약관
              </a>
              <a href="#" className="font-bold">
                개인정보처리방침
              </a>
              <div className="mt-1">VeriSign 256-bit SSL 암호화 적용</div>
            </div>
            <div>
              <img
                src="/images/PASS/copyright_small.png"
                alt="KCB"
                className="h-6"
              />
            </div>
          </div>
        </div>
      </footer>

      {/* 알뜰폰 레이어 팝업 */}
      <Dialog open={mvnoPopupOpen} onOpenChange={setMvnoPopupOpen}>
        <DialogContent className="max-h-fit w-full max-w-lg bg-white">
          <DialogHeader className="sr-only">
            <DialogTitle>알뜰폰 사업자</DialogTitle>
            <DialogDescription>알뜰폰 사업자 목록</DialogDescription>
          </DialogHeader>
          <div className="max-h-[80vh] overflow-y-auto">
            <ul className="space-y-4">
              {/* SKT 알뜰폰 */}
              <li className="rounded border p-3">
                <div className="mb-2 flex items-center">
                  <input
                    type="radio"
                    name="mobilecoPop"
                    id="agency-popup-sk"
                    value="04"
                    checked={selectedMvno === "04"}
                    onChange={() => handleMvnoSelect("04")}
                    className="mr-2"
                  />
                  <label
                    htmlFor="agency-popup-sk"
                    className="flex items-center"
                  >
                    <img
                      src="/images/PASS/logo_sk.png"
                      alt="SK telecom"
                      className="mr-2 h-6"
                    />
                    <span>사업자목록</span>
                  </label>
                </div>
                <div className="pl-6 text-xs text-gray-600">
                  <p>
                    티플러스, 아이즈모바일, 모빙, 이야기모바일, 스마텔,
                    SK세븐모바일, 이마트알뜰폰, 안심모바일, 프리티, 헬로모바일,
                    조이텔, 리브모바일, 토스모바일, 스테이지파이브
                  </p>
                </div>
              </li>
              {/* KT 알뜰폰 */}
              <li className="rounded border p-3">
                <div className="mb-2 flex items-center">
                  <input
                    type="radio"
                    name="mobilecoPop"
                    id="agency-popup-kt"
                    value="05"
                    checked={selectedMvno === "05"}
                    onChange={() => handleMvnoSelect("05")}
                    className="mr-2"
                  />
                  <label
                    htmlFor="agency-popup-kt"
                    className="flex items-center"
                  >
                    <img
                      src="/images/PASS/logo_kt.png"
                      alt="KT"
                      className="mr-2 h-6"
                    />
                    <span>사업자목록</span>
                  </label>
                </div>
                <div className="pl-6 text-xs text-gray-600">
                  <p>
                    LG헬로비전(헬로모바일), 세종텔레콤(스노우맨),
                    씨앤커뮤니케이션(WMVNO), 에넥스텔레콤(A mobile),
                    프리텔레콤(프리티), 코드모바일(이지모바일), KT파워텔,
                    위너스텔(Well), ACN코리아(플래시모바일),
                    앤알커뮤니케이션(앤텔레콤), 에스원(안심모바일),
                    아이즈비전(아이즈모바일), KT M모바일(M모바일),
                    유니컴즈(모빙), 더원플랫폼(IplusU),
                    스테이지파이브(핀플레이), 드림라인(드림모바일),
                    한국케이블텔레콤(TPLUS), 와이엘랜드(여유알뜰폰),
                    큰사람커넥트(이야기모바일), 니즈텔레콤(니즈텔레콤),
                    KT스카이라이프(스카이라이프모바일),
                    에이프러스(아시아모바일), 한국피엠오(주)(밸류컴),
                    (주)스마텔, 토스모바일, (주)고고팩토리, (주)에르엘,
                    (주)핀샷, (주)오파스넷, (주)친구아이앤씨,
                    (주)한패스인터내셔널, (주)한국이텔레콤, 스피츠모바일(주)
                  </p>
                </div>
              </li>

              {/* LGU+ 알뜰폰 */}
              <li className="rounded border p-3">
                <div className="mb-2 flex items-center">
                  <input
                    type="radio"
                    name="mobilecoPop"
                    id="agency-popup-lgu"
                    value="06"
                    checked={selectedMvno === "06"}
                    onChange={() => handleMvnoSelect("06")}
                    className="mr-2"
                  />
                  <label
                    htmlFor="agency-popup-lgu"
                    className="flex items-center"
                  >
                    <img
                      src="/images/PASS/logo_lgu.png"
                      alt="LG U+"
                      className="mr-2 h-6"
                    />
                    <span>사업자목록</span>
                  </label>
                </div>
                <div className="pl-6 text-xs text-gray-600">
                  <p>
                    ACN코리아(플래시모바일), (주)에넥스텔레콤(A모바일),
                    (주)에이프러스(아시아모바일), (주)CK커뮤스트리(슈가모바일),
                    SL리테일(셀모바일), (주)코드모바일(이지모바일),
                    에르엘모바일, (주)아이즈비전(아이즈모바일), (주)핀샷,
                    LG헬로비전(헬로모바일), 한패스모바일, (주)보스(화인통신),
                    사람과연결, 인스코리아, 조이텔, 국민은행(리브모바일),
                    제주방송, (주)코나아이, (주)KG모빌리언스(KG모바일),
                    (주)큰사람커넥트(이야기모바일), 토스모바일,
                    (주)미디어로그(U+유모바일), (주)마블프로듀스(마블링),
                    니즈텔레콤, (주)앤알커뮤니케이션(앤텔레콤), 엔티온텔레콤,
                    원텔레콤, (주)스테이지파이브(핀플레이),
                    한국피엠오㈜(벨류컴), (주)레그원(온국민폰),
                    에스원안심모바일, 서경모바일, 세종텔레콤(스노우맨), 스마텔,
                    (주)인스코비(프리티), (주)한국케이블텔레콤(티플러스),
                    (주)유니컴즈(모빙), (주)와이드모바일(도시락모바일),
                    (주)위너스텔(well), (주)와이엘랜드(여유텔레콤),
                    글로벌머니익스프레스(GME모바일),
                    (주)친구아이앤씨(친구모바일), 찬스모바일
                  </p>
                </div>
              </li>
            </ul>
          </div>
          <div className="flex border-t">
            <button
              type="button"
              className="flex-1 border-r py-3 text-gray-700"
              onClick={() => setMvnoPopupOpen(false)}
            >
              취소
            </button>
            <button
              type="button"
              className="flex-1 py-3 font-bold text-blue-600"
              onClick={handleMvnoConfirm}
            >
              선택
            </button>
          </div>
        </DialogContent>
      </Dialog>

      {/* 약관 레이어 팝업 */}
      <Dialog open={agreePopupOpen} onOpenChange={setAgreePopupOpen}>
        <DialogContent className="max-h-fit w-full max-w-lg bg-white">
          <DialogHeader className="sr-only">
            <DialogTitle>약관</DialogTitle>
            <DialogDescription>
              {currentAgreement === "agree1" && "개인정보이용동의"}
              {currentAgreement === "agree2" && "고유식별정보처리동의"}
              {currentAgreement === "agree3" && "서비스이용약관동의"}
              {currentAgreement === "agree4" && "통신사이용약관동의"}
              {currentAgreement === "agree5" && "제3자 정보제공 동의"}
            </DialogDescription>
          </DialogHeader>
          <div className="max-h-[80vh] overflow-y-auto">
            <div className="mb-4 h-80 overflow-y-auto border border-gray-300 p-4">
              <iframe
                id="agreeIframe"
                src=""
                frameBorder="0"
                className="h-full w-full"
              ></iframe>
            </div>
          </div>
        </DialogContent>
      </Dialog>

      {/* 인증 레이어 팝업 */}
      <Dialog open={authPopupOpen} onOpenChange={setAuthPopupOpen}>
        <DialogContent className="max-h-fit w-full max-w-lg bg-white">
          <DialogHeader>
            <DialogTitle className="flex justify-center">
              <img
                src="/images/PASS/logo_pass.png"
                alt="PASS"
                className="h-8"
              />
            </DialogTitle>
            <DialogDescription className="sr-only">
              휴대폰 본인확인 입력
            </DialogDescription>
          </DialogHeader>
          <div className="max-h-[80vh] overflow-y-auto">
            <div className="p-4">
              <form className="mb-6">
                <fieldset>
                  <legend className="sr-only">휴대폰 본인확인 입력</legend>
                  <ul className="space-y-4">
                    <li>
                      <h3 className="mb-1 font-bold text-black">이름</h3>
                      <div className="relative">
                        <input
                          type="text"
                          name="userName"
                          id="userName"
                          placeholder="성명입력"
                          maxLength={50}
                          value={userName}
                          onChange={handleInputChange}
                          className="w-full rounded border border-gray-300 p-2 text-black"
                        />
                        {userName && (
                          <button
                            type="button"
                            className="absolute top-1/2 right-2 -translate-y-1/2 transform text-gray-500"
                            onClick={() => onUserNameChange("")}
                          >
                            ×
                          </button>
                        )}
                      </div>
                    </li>
                    <li>
                      <h3 className="mb-1 font-bold text-black">휴대폰번호</h3>
                      <div className="relative">
                        <input
                          type="tel"
                          name="phoneNumber"
                          id="phoneNumber"
                          placeholder="숫자만 입력"
                          maxLength={11}
                          value={phoneNumber}
                          onChange={handleInputChange}
                          className="w-full rounded border border-gray-300 p-2 text-black"
                        />
                        {phoneNumber && (
                          <button
                            type="button"
                            className="absolute top-1/2 right-2 -translate-y-1/2 transform text-gray-500"
                            onClick={() => onPhoneNumberChange("")}
                          >
                            ×
                          </button>
                        )}
                      </div>
                    </li>
                  </ul>
                </fieldset>
              </form>

              <div className="mb-4 flex">
                <button
                  type="button"
                  className="mr-2 flex-1 rounded border border-gray-300 bg-gray-100 py-3 font-bold text-gray-700"
                  onClick={() => {
                    onUserNameChange("");
                    onPhoneNumberChange("");
                    setAuthPopupOpen(false);
                  }}
                >
                  취소
                </button>
                <button
                  type="button"
                  onClick={handleSubmitPass}
                  className={`flex-1 rounded py-3 font-bold text-white ${
                    userName && phoneNumber.length >= 10
                      ? "bg-red-600"
                      : "bg-gray-400"
                  }`}
                  disabled={!userName || phoneNumber.length < 10}
                >
                  확인
                </button>
              </div>

              <div className="rounded bg-gray-100 p-4 text-sm text-gray-700">
                <p>
                  PASS앱 설치 및 가입 후 이용이 가능합니다. <br />
                  앱마켓(구글 플레이스토어 / 애플 앱스토어) 에서
                  <span className="font-bold text-red-600"> "PASS" </span>
                  검색!
                </p>
              </div>
            </div>

            <div className="mt-4 w-full">
              <a
                href="https://fido.kt.com/ktauthIntro"
                target="_blank"
                rel="noopener noreferrer"
              >
                <img
                  src="/images/PASS/kt_pass_banner_pc_20230926.png"
                  alt="PASS 인증을 넘어 일상으로 PASS"
                  className="w-full"
                />
              </a>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default PASS;
