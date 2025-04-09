import SignatureLoader from "@/shared/ui/SignatureLoader";

interface LoadingOverlayProps {
  isLoading: boolean;
  content?: string;
}

const LoadingOverlay = ({ isLoading, content }: LoadingOverlayProps) => {
  if (!isLoading) return null;

  return (
    <div className="fixed inset-0 z-[9999] flex flex-col items-center justify-center bg-black/50 backdrop-blur-sm">
      <SignatureLoader />
      <p className="text-2xl text-white">{content}</p>
    </div>
  );
};

export default LoadingOverlay;
