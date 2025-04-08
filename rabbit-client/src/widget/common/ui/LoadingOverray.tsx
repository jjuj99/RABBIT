import SignatureLoader from "@/shared/ui/SignatureLoader";

interface LoadingOverlayProps {
  isLoading: boolean;
}

const LoadingOverlay = ({ isLoading }: LoadingOverlayProps) => {
  if (!isLoading) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
      <SignatureLoader />
    </div>
  );
};

export default LoadingOverlay;
