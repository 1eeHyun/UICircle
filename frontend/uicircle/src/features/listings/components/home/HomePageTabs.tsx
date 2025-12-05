interface HomePageTabsProps {
  activeTab: "RECENT" | "LIKES";
  onChange: (tab: "RECENT" | "LIKES") => void;
}

export default function HomePageTabs({ activeTab, onChange }: HomePageTabsProps) {
  const tabs = [
    { key: "RECENT", label: "Recent" },
    { key: "LIKES", label: "Likes" },
  ] as const;

  return (
    <div 
      className="
        flex gap-10 
        border-b 
        text-base font-semibold text-gray-600         
        mt-4
      "
    >
      {tabs.map((t) => {
        const isActive = t.key === activeTab;
        return (
          <button
            key={t.key}
            onClick={() => onChange(t.key)}
            className={`
              pb-4 
              transition
              ${
                isActive
                  ? "text-black border-b-2 border-black"
                  : "text-gray-500 hover:text-gray-800"
              }
            `}
          >
            {t.label}
          </button>
        );
      })}
    </div>
  );
}
