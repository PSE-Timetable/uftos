export type ApiError = {
  timestamp: string;
  status: number;
  error: string;
  trace: string;
  message: string;
  errors: [
    { code: string; codes: string[]; defaultMessage: string; objectName: string; field: string; rejectedValue: string },
  ];
};

export const parseError = (error: unknown) => {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  return JSON.parse((error as any).data) as ApiError;
};
