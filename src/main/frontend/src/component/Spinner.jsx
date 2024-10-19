import React from "react";

const Spinner = () => {
  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="animate-spin inline-block w-24 h-24 border-4 border-t-4 border-t-blue-500 border-gray-200 rounded-full"></div>
    </div>
  );
};

export default Spinner;
