import logoUrl from '@/assets/logo.png';

interface Props {
  size?: number;
}

export function NmLogoMark({ size = 28 }: Props) {
  return (
    <img
      src={logoUrl}
      alt="NyangMunity"
      width={size}
      height={size}
      className="object-contain shrink-0"
      style={{ width: size, height: size }}
    />
  );
}

export function NmLogo() {
  return (
    <span className="nm-logo">
      <NmLogoMark />
      <span className="nm-logo-name">
        Nyang<em>munity</em>
      </span>
    </span>
  );
}
