import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IconComponent } from '../../../shared/components/icon/icon.component';
import { AdminService } from '../../../core/services/admin.service';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, FormsModule, IconComponent],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.scss',
})
export class SettingsComponent implements OnInit {
  activeTab = 'general';

  tabs = [
    { id: 'general', label: 'General', icon: 'settings' },
    { id: 'appearance', label: 'Appearance', icon: 'globe' },
    { id: 'email', label: 'Email Templates', icon: 'file-text' },
    { id: 'features', label: 'Feature Flags', icon: 'check-circle' },
  ];

  // General
  platformName = '';
  supportEmail = '';

  // Appearance
  selectedTheme = 'Ocean Breeze';
  darkMode = false;
  heroTitle = '';
  heroSubtitle = '';

  // UI options (not data — these are the available theme choices)
  themes = [
    { name: 'Ocean Breeze', primary: '#0077B6' },
    { name: 'Forest', primary: '#2D6A4F' },
    { name: 'Sunset', primary: '#E76F51' },
    { name: 'Royal', primary: '#6930C3' },
    { name: 'Midnight', primary: '#1D3557' },
  ];

  // Email Templates
  emailTemplates: { key: string; name: string; description: string }[] = [];

  // Feature Flags
  featureFlags: { key: string; name: string; description: string; enabled: boolean }[] = [];

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.adminService.getPlatformSettings().subscribe({
      next: (settings) => {
        this.platformName = settings.platformName || '';
        this.supportEmail = settings.supportEmail || '';
        this.selectedTheme = settings.selectedTheme || 'Ocean Breeze';
        this.darkMode = settings.darkMode || false;
        this.heroTitle = settings.heroTitle || '';
        this.heroSubtitle = settings.heroSubtitle || '';
      },
      error: () => {},
    });

    this.adminService.getEmailTemplates().subscribe({
      next: (templates) => (this.emailTemplates = templates),
      error: () => {},
    });

    this.adminService.getFeatureFlags().subscribe({
      next: (flags) => (this.featureFlags = flags),
      error: () => {},
    });
  }
}
