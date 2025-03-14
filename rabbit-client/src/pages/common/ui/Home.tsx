import { Button } from "@/shared/ui/button";

const Home = () => {
  return (
    <main className="flex h-full w-full items-center justify-center gap-4 py-10">
      <Button
        onClick={() => {
          throw new Error("에러를 발생시켜보자");
        }}
        className="btn-glass font-partial h-10 w-48"
      >
        Break the world
      </Button>
    </main>
  );
};

export default Home;
