import SignatureLoader from "./SignatureLoader";
const DefaultLoadingFallback = () => (
  <div className="flex min-h-[50vh] w-full flex-col items-center justify-center">
    {/* <div className="loader-sprite" /> */}
    {/* <Loader2 className="h-8 w-8 animate-spin" /> */}
    <SignatureLoader />
  </div>
);

export default DefaultLoadingFallback;
