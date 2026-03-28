import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class ProfileScreen extends StatelessWidget {
  const ProfileScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Profile')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          Card(
            child: Padding(
              padding: const EdgeInsets.all(20),
              child: Column(
                children: [
                  const CircleAvatar(radius: 40, child: Icon(Icons.person, size: 40)),
                  const SizedBox(height: 12),
                  const Text('John Doe', style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
                  Text('Member #MBR-123456', style: TextStyle(color: Colors.grey[600])),
                  const SizedBox(height: 4),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
                    decoration: BoxDecoration(color: Colors.green.shade50, borderRadius: BorderRadius.circular(12)),
                    child: const Text('Active', style: TextStyle(color: Colors.green, fontWeight: FontWeight.w600)),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),
          const Text('Personal Information', style: TextStyle(fontSize: 16, fontWeight: FontWeight.w600)),
          const SizedBox(height: 8),
          _infoTile(Icons.email, 'Email', 'john.doe@email.com'),
          _infoTile(Icons.phone, 'Phone', '+1 234 567 890'),
          _infoTile(Icons.cake, 'Date of Birth', 'January 15, 1990'),
          _infoTile(Icons.home, 'Address', '123 Main Street, Harare'),
          const SizedBox(height: 16),
          const Text('Dependants', style: TextStyle(fontSize: 16, fontWeight: FontWeight.w600)),
          const SizedBox(height: 8),
          _dependantTile('Jane Doe', 'Spouse', 'Active'),
          _dependantTile('Tom Doe', 'Child', 'Active'),
          const SizedBox(height: 16),
          const Text('Scheme', style: TextStyle(fontSize: 16, fontWeight: FontWeight.w600)),
          const SizedBox(height: 8),
          Card(child: ListTile(
            leading: const Icon(Icons.health_and_safety),
            title: const Text('Gold Medical Aid'),
            subtitle: const Text('Enrolled since Jan 2024'),
            trailing: const Icon(Icons.chevron_right),
          )),
          const SizedBox(height: 24),
          OutlinedButton.icon(
            onPressed: () => context.go('/login'),
            icon: const Icon(Icons.logout, color: Colors.red),
            label: const Text('Logout', style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );
  }

  Widget _infoTile(IconData icon, String label, String value) {
    return Card(
      margin: const EdgeInsets.only(bottom: 4),
      child: ListTile(
        leading: Icon(icon, size: 20, color: Colors.grey[600]),
        title: Text(label, style: TextStyle(fontSize: 12, color: Colors.grey[500])),
        subtitle: Text(value, style: const TextStyle(fontSize: 15)),
        dense: true,
      ),
    );
  }

  Widget _dependantTile(String name, String relationship, String status) {
    return Card(
      margin: const EdgeInsets.only(bottom: 4),
      child: ListTile(
        leading: const CircleAvatar(radius: 16, child: Icon(Icons.person, size: 16)),
        title: Text(name),
        subtitle: Text(relationship),
        trailing: Text(status, style: const TextStyle(color: Colors.green, fontWeight: FontWeight.w600)),
      ),
    );
  }
}
