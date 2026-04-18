export interface NavItem {
  label: string;
  route: string;
  icon: string;
  roles: string[];
  children?: NavItem[];
  badge?: string;
}

export interface NavGroup {
  title?: string;
  items: NavItem[];
}

export interface UserInfo {
  fullName: string;
  initials: string;
  email: string;
  roleLabel: string;
}
