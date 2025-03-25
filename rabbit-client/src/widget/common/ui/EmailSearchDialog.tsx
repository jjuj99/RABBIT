import { Button } from "@/shared/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/shared/ui/dialog";
import { Input } from "@/shared/ui/input";
import { useState } from "react";

interface User {
  id: string;
  email: string;
  name: string;
  wallet: string;
}
interface EmailSearchDialogProps {
  children: React.ReactNode;
  title: string;
  description: string;
  open: boolean;
  setOpen: (open: boolean) => void;
  onCancel?: () => void;
  onUserSelect: (user: User) => void;
  cancelText?: string;
  searchText?: string;
}

const EmailSearchDialog = ({
  children,
  title,
  description,
  open,
  setOpen,
  onCancel,
  onUserSelect,
  cancelText = "취소",
  searchText = "검색",
}: EmailSearchDialogProps) => {
  const [email, setEmail] = useState("");
  const [user, setUser] = useState<User | null>(null);
  const [isSearching, setIsSearching] = useState(false);
  const [hasSearched, setHasSearched] = useState(false);
  const [notFound, setNotFound] = useState(false);

  const handleSearch = async () => {
    if (!email) return;

    setIsSearching(true);
    setHasSearched(true);
    setNotFound(false);
    setUser(null);

    try {
      // 이 부분은 실제 API 호출로 대체해야 합니다
      // const response = await fetch(`/api/users/search?email=${encodeURIComponent(email)}`);
      // if (response.ok) {
      //   const data = await response.json();
      //   if (data.user) {
      //     setUser(data.user);
      //   } else {
      //     setNotFound(true);
      //   }
      // } else {
      //   setNotFound(true);
      // }

      // 임시 데이터 - 정확히 일치하는 이메일만 찾기
      const mockUsers: User[] = [
        {
          id: "1",
          email: "user1@example.com",
          name: "사용자1",
          wallet: "0x123...",
        },
        {
          id: "2",
          email: "user2@example.com",
          name: "사용자2",
          wallet: "0x45612314123...",
        },
      ];

      const foundUser = mockUsers.find((u) => u.email === email);

      if (foundUser) {
        setUser(foundUser);
      } else {
        setNotFound(true);
      }
    } catch (error) {
      console.error("사용자 검색 중 오류 발생:", error);
      setNotFound(true);
    } finally {
      setIsSearching(false);
    }
  };

  const handleUserSelect = () => {
    if (user) {
      onUserSelect(user);
      setOpen(false);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      e.preventDefault();
      handleSearch();
    }
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>{children}</DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{title}</DialogTitle>
          <DialogDescription>{description}</DialogDescription>
        </DialogHeader>

        <div className="mt-4 flex items-center gap-2">
          <Input
            type="email"
            placeholder="이메일을 입력하세요"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            onKeyDown={handleKeyDown}
            className="flex-1"
          />
          <Button onClick={handleSearch} disabled={isSearching || !email}>
            {searchText}
          </Button>
        </div>

        <div className="mt-4 flex min-h-[100px] items-center justify-center">
          {isSearching ? (
            <p className="text-center">검색 중...</p>
          ) : user ? (
            <div className="w-full rounded border border-gray-700 p-4">
              <div className="flex flex-col gap-2">
                <p className="text-lg font-medium">{user.name}</p>
                <p className="text-gray-200">{user.email}</p>
                {user.wallet && (
                  <p className="text-sm text-gray-400">지갑: {user.wallet}</p>
                )}
                <Button
                  variant="primary"
                  className="mt-2"
                  onClick={handleUserSelect}
                >
                  선택
                </Button>
              </div>
            </div>
          ) : notFound ? (
            <p className="text-center text-gray-500">
              "{email}"을 가진 사용자를 찾을 수 없습니다
            </p>
          ) : hasSearched ? (
            <p className="text-center text-gray-500">
              사용자를 찾을 수 없습니다
            </p>
          ) : (
            <p className="text-center text-gray-500">
              정확한 이메일을 입력하고 검색 버튼을 눌러주세요
            </p>
          )}
        </div>

        <div className="mt-4 flex justify-end gap-3">
          {onCancel && (
            <Button variant="secondary" onClick={onCancel}>
              {cancelText}
            </Button>
          )}
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default EmailSearchDialog;
