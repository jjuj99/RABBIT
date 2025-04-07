interface DataCardProps<T> {
  item: T;
  onClick?: (item: T) => void;
  renderHeader: (item: T) => React.ReactNode;
  sections: {
    title: string;
    items: {
      label: string;
      render: (item: T) => React.ReactNode;
    }[];
  }[];
}

export function DataCard<T>({
  item,
  onClick,
  renderHeader,
  sections,
}: DataCardProps<T>) {
  return (
    <div
      onClick={() => onClick?.(item)}
      className="cursor-pointer rounded-lg border border-gray-700/50 bg-gray-800/30 p-4 shadow-md transition-transform hover:scale-[1.02] active:scale-[0.98]"
    >
      <div className="mb-3">{renderHeader(item)}</div>

      <div className="space-y-4 text-sm">
        {sections.map((section, index) => (
          <div key={index} className="space-y-2">
            {section.title && (
              <h3 className="text-sm font-medium text-gray-400">
                {section.title}
              </h3>
            )}
            <div className="grid grid-cols-2 gap-2">
              {section.items.map((field, fieldIndex) => (
                <div key={fieldIndex} className="space-y-1">
                  <div className="text-gray-400">{field.label}</div>
                  <div className="text-white">{field.render(item)}</div>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
